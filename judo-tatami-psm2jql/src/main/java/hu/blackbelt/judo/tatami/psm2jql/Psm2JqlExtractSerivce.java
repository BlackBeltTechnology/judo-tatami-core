package hu.blackbelt.judo.tatami.psm2jql;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.registerPsmMetamodel;
import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.*;

@Component(immediate = true, service = Psm2JqlExtractSerivce.class)
@Slf4j
public class Psm2JqlExtractSerivce {

    public static final String PSM_JQL_EXTRACT_META_VERSION_RANGE = "Psm-Jql-Extract-Meta-Version-Range";

    @Reference
    Psm2JqlExtractScriptResource psm2JqlExtractScriptResource;

    public PsmJqlExtractModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createPsmJqlExtractResourceSet(bundleURIHandler);

        PsmJqlExtractModel psmJqlExtractModel = loadPsmJqlExtractModel(resourceSet, URI.createURI("urn:" + psmModel.getName()),
                psmModel.getName(), psmModel.getVersion(), psmModel.getChecksum(),
                bundleContext.getBundle().getHeaders().get(PSM_JQL_EXTRACT_META_VERSION_RANGE));

        registerPsmMetamodel(resourceSet);

        Psm2JqlExtract.executePsm2PsmJqlExtractTransformation(resourceSet, psmModel, psmJqlExtractModel, new Slf4jLog(log),
                new File(psm2JqlExtractScriptResource.getSctiptRoot().getAbsolutePath(), "psm2jql/transformations/resource/") );

        return psmJqlExtractModel;
    }
}
