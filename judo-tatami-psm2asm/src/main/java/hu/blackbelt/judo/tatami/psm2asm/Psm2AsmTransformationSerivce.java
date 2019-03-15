package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.AsmModel;
import hu.blackbelt.judo.meta.asm.AsmPackageRegistration;
import hu.blackbelt.judo.meta.psm.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.asm.AsmModelLoader.*;
import static hu.blackbelt.judo.meta.psm.PsmModelLoader.registerPsmMetamodel;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;


@Component(immediate = true, service = Psm2AsmTransformationSerivce.class)
@Slf4j
public class Psm2AsmTransformationSerivce {

    public static final String ASM_META_VERSION_RANGE = "Asm-Meta-Version-Range";

    @Reference
    AsmPackageRegistration asmPackageRegistration;

    @Reference
    Psm2AsmTransformationSctiptResource psm2AsmTransformationSctiptResource;


    public AsmModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createAsmResourceSet();
        resourceSet.getURIConverter().getURIHandlers().add(0, bundleURIHandler);

        AsmModel asmModel = loadAsmModel(resourceSet, asmPackageRegistration, URI.createURI("urn:" + psmModel.getName()),
                psmModel.getName(), psmModel.getVersion(), psmModel.getChecksum(),
                bundleContext.getBundle().getHeaders().get(ASM_META_VERSION_RANGE));

        registerPsmMetamodel(resourceSet);

        executePsm2AsmTransformation(resourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(psm2AsmTransformationSctiptResource.getSctiptRoot().getAbsolutePath(), "psm2asm/transformations/resource/") );

        return asmModel;
    }
}
