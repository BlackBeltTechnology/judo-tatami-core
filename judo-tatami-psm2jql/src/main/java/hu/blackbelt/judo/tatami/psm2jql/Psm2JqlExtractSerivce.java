package hu.blackbelt.judo.tatami.psm2jql;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
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

import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.createPsmJqlExtractResourceSet;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.registerPsmMetamodel;
import static hu.blackbelt.judo.tatami.psm2jql.Psm2JqlExtract.executePsm2PsmJqlExtractTransformation;

@Component(immediate = true, service = Psm2JqlExtractSerivce.class)
@Slf4j
public class Psm2JqlExtractSerivce {

    public static final String PSM_JQL_EXTRACT_META_VERSION_RANGE = "Psm-Jql-Extract-Meta-Version-Range";

    @Reference
    Psm2JqlExtractScriptResource psm2JqlExtractScriptResource;

    Map<PsmModel, ServiceRegistration<TransformationTrace>> psm2JqlExtractTransformationTraceRegistration = Maps.newHashMap();


    public PsmJqlExtractModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {

        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet psmJqlExtractResourceSet = createPsmJqlExtractResourceSet(bundleURIHandler);
        registerPsmMetamodel(psmJqlExtractResourceSet);

        PsmJqlExtractModel psmJqlExtractModel = PsmJqlExtractModel.buildPsmJqlExtractModel()
                .name(psmModel.getName())
                .version(psmModel.getVersion())
                .uri(URI.createURI("urn:" + psmModel.getName() + ".jqlextract"))
                .checksum(psmModel.getChecksum())
                .resourceSet(psmJqlExtractResourceSet)
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(PSM_JQL_EXTRACT_META_VERSION_RANGE)).build();

        Psm2JqlExtractTransformationTrace psm2JqlExtractTransformationTrace = executePsm2PsmJqlExtractTransformation(psmJqlExtractResourceSet, psmModel, psmJqlExtractModel, new Slf4jLog(log),
                new File(psm2JqlExtractScriptResource.getSctiptRoot().getAbsolutePath(), "psm2jql/transformations/jql/") );

        psm2JqlExtractTransformationTraceRegistration.put(psmModel, bundleContext.registerService(TransformationTrace.class, psm2JqlExtractTransformationTrace, new Hashtable<>()));
        return psmJqlExtractModel;
    }

    public void uninstall(PsmModel psmModel) {
        if (psm2JqlExtractTransformationTraceRegistration.containsKey(psmModel)) {
            psm2JqlExtractTransformationTraceRegistration.get(psmModel).unregister();
        } else {
            log.error("PSM model is not installed: " + psmModel.toString());
        }
    }

}
