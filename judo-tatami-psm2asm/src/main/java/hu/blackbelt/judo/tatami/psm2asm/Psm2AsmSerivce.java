package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import hu.blackbelt.judo.meta.asm.runtime.AsmPackageRegistration;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
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

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.registerPsmMetamodel;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;


@Component(immediate = true, service = Psm2AsmSerivce.class)
@Slf4j
public class Psm2AsmSerivce {

    public static final String ASM_META_VERSION_RANGE = "Asm-Meta-Version-Range";

    @Reference
    AsmPackageRegistration asmPackageRegistration;

    @Reference
    Psm2AsmScriptResource psm2AsmScriptResource;

    Map<PsmModel, ServiceRegistration<TrackInfo>> psm2AsmTrackInfoRegistration = Maps.newHashMap();


    public AsmModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {

        ResourceSet resourceSet = createAsmResourceSet(new BundleURIHandler("urn", "", bundleContext.getBundle()));
        AsmModelLoader.registerAsmPackages(resourceSet, asmPackageRegistration);
        registerPsmMetamodel(resourceSet);

        AsmModel asmModel = AsmModel.asmModelBuilder()
                .name(psmModel.getName())
                .version(psmModel.getVersion())
                .uri(URI.createURI("urn:" + psmModel.getName() + ".asm"))
                .checksum(psmModel.getChecksum())
                .resourceSet(resourceSet)
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(ASM_META_VERSION_RANGE)).build();

        Psm2AsmTrackInfo trackInfo = executePsm2AsmTransformation(resourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(psm2AsmScriptResource.getSctiptRoot().getAbsolutePath(), "psm2asm/transformations/asm/"));

        psm2AsmTrackInfoRegistration.put(psmModel, bundleContext.registerService(TrackInfo.class, trackInfo, new Hashtable<>()));
        return asmModel;
    }


    public void uninstall(PsmModel psmModel) {
        if (psm2AsmTrackInfoRegistration.containsKey(psmModel)) {
            psm2AsmTrackInfoRegistration.get(psmModel).unregister();
        } else {
            log.error("PSM model is not installed: " + psmModel.toString());
        }
    }

}
