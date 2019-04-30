package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmModelLoader;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModelLoader.createEsmResourceSet;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;

@Component(immediate = true, service = Esm2PsmService.class)
@Slf4j
public class Esm2PsmService {

    public static final String PSM_META_VERSION_RANGE = "Psm-Meta-Version-Range";

    @Reference
    Esm2PsmScriptResource esm2PsmScriptResource;

    Map<EsmModel, ServiceRegistration<TransformationTrace>> esm2PsmTransformationTraceRegistration = Maps.newHashMap();


    public PsmModel install(EsmModel esmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createEsmResourceSet(bundleURIHandler);
        EsmModelLoader.registerEsmMetamodel(resourceSet);

        PsmModel psmModel = PsmModel.buildPsmModel()
                .name(esmModel.getName())
                .version(esmModel.getVersion())
                .uri(URI.createURI("urn:" + esmModel.getName() + ".psm"))
                .checksum(esmModel.getChecksum())
                .resourceSet(resourceSet)
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(PSM_META_VERSION_RANGE)).build();

        Esm2PsmTransformationTrace transformationTrace = executeEsm2PsmTransformation(resourceSet, esmModel, psmModel, new Slf4jLog(log),
                new File(esm2PsmScriptResource.getScriptRoot().getAbsolutePath(), "esm2psm/transformations/psm/"));

        esm2PsmTransformationTraceRegistration.put(esmModel, bundleContext.registerService(TransformationTrace.class, transformationTrace, new Hashtable<>()));
        return psmModel;
    }


    public void uninstall(EsmModel esmModel) {
        if (esm2PsmTransformationTraceRegistration.containsKey(esmModel)) {
            esm2PsmTransformationTraceRegistration.get(esmModel).unregister();
        } else {
            log.error("PSM model is not installed: " + esmModel.toString());
        }
    }

}
