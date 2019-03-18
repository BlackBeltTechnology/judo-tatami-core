package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmPackageRegistration;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.*;
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


    public AsmModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createAsmResourceSet(bundleURIHandler);

        AsmModel asmModel = loadAsmModel(resourceSet, asmPackageRegistration, URI.createURI("urn:" + psmModel.getName()),
                psmModel.getName(), psmModel.getVersion(), psmModel.getChecksum(),
                bundleContext.getBundle().getHeaders().get(ASM_META_VERSION_RANGE));

        registerPsmMetamodel(resourceSet);

        executePsm2AsmTransformation(resourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(psm2AsmScriptResource.getSctiptRoot().getAbsolutePath(), "psm2asm/transformations/resource/") );

        return asmModel;
    }
}
