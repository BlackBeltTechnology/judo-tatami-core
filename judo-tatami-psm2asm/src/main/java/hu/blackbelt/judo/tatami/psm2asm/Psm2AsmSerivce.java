package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.registerPsmMetamodel;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;


@Component(immediate = true, service = Psm2AsmSerivce.class)
@Slf4j
public class Psm2AsmSerivce {

    public static final String ASM_META_VERSION_RANGE = "Asm-Meta-Version-Range";

    Map<PsmModel, ServiceRegistration<TransformationTrace>> psm2AsmTransformationTraceRegistration = Maps.newHashMap();

    BundleContext scriptBundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        scriptBundleContext = bundleContext;
    }

    public AsmModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {

        ResourceSet resourceSet = createAsmResourceSet(new BundleURIHandler("urn", "", bundleContext.getBundle()));
        registerPsmMetamodel(resourceSet);

        AsmModel asmModel = AsmModel.asmModelBuilder()
                .name(psmModel.getName())
                .version(psmModel.getVersion())
                .uri(URI.createURI("urn:" + psmModel.getName() + ".asm"))
                .checksum(psmModel.getChecksum())
                .resourceSet(resourceSet)
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(ASM_META_VERSION_RANGE)).build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/psm2asm/transformations/asm/psmToAsm.etl")
                            .toURI()
                            .resolve(".");

            Psm2AsmTransformationTrace transformationTrace = executePsm2AsmTransformation(resourceSet, psmModel, asmModel, logger,
                    scriptUri);

            psm2AsmTransformationTraceRegistration.put(psmModel, bundleContext.registerService(TransformationTrace.class, transformationTrace, new Hashtable<>()));
            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }
        return asmModel;
    }


    public void uninstall(PsmModel psmModel) {
        if (psm2AsmTransformationTraceRegistration.containsKey(psmModel)) {
            psm2AsmTransformationTraceRegistration.get(psmModel).unregister();
        } else {
            log.error("PSM model is not installed: " + psmModel.toString());
        }
    }

}
