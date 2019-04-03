package hu.blackbelt.judo.tatami.psm2jql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.createPsmJqlExtractResourceSet;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.createPsmResourceSet;
import static hu.blackbelt.judo.tatami.psm2jql.Psm2JqlExtract.*;

@Slf4j
public class Psm2JqlExtractTest {

    public static final String PSM_2_JQLEXTRACT_MODEL = "psm2jqlextract.model";
    public static final String TRACE_PSM_2_JQLEXTRACT = "trace:psm2jqlextract";
    public static final String PSM_NORTHWIND = "psm:northwind";
    public static final String JQLEXTRACT_NORTHWIND = "jqlextract:northwind";
    public static final String URN_NORTHWIND_JUDOPSM_MODEL = "urn:northwind-judopsm.model";
    public static final String URN_NORTHWIND_JQLEXTRACT = "urn:northwind-jqlextract.model";

    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), targetDir().getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(PSM_NORTHWIND), URI.createURI(URN_NORTHWIND_JUDOPSM_MODEL),
                        URI.createURI(JQLEXTRACT_NORTHWIND), URI.createURI(URN_NORTHWIND_JQLEXTRACT))
        );


        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet psmResourceSet = createPsmResourceSet(uriHandler);
        psmModel = PsmModelLoader.loadPsmModel(
                psmResourceSet,
                URI.createURI(PSM_NORTHWIND),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testPsm2JqlExtractTransformation() throws Exception {

        // Creating PSM JQL extract resource set.
        ResourceSet psmJqlExtractResourceSet = createPsmJqlExtractResourceSet(uriHandler);


        PsmJqlExtractModel jqlExtractModel = PsmJqlExtractModel.buildPsmJqlExtractModel()
                .name(psmModel.getName())
                .resourceSet(psmJqlExtractResourceSet)
                .uri(URI.createURI(JQLEXTRACT_NORTHWIND))
                .version(psmModel.getVersion())
                .build();

        // Make transformation which returns the track with the serialized URI's
        Psm2JqlExtractTrackInfo psm2JqlExtractTrackInfo = executePsm2PsmJqlExtractTransformation(psmJqlExtractResourceSet, psmModel, jqlExtractModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations/jql"));


        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getPsm2JqlExtractTrace(psm2JqlExtractTrackInfo.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(targetDir().getAbsolutePath(), PSM_2_JQLEXTRACT_MODEL)), ImmutableMap.of());

        // Loadeing trace map
        ResourceSet traceLoadedResourceSet = createPsm2JqlExtractTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_PSM_2_JQLEXTRACT));
        traceResoureLoaded.load(new FileInputStream(new File(targetDir().getAbsolutePath(), PSM_2_JQLEXTRACT_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolvePsm2JqlExtractTrace(traceResoureLoaded, psmModel, jqlExtractModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.info(e.toString() + " -> " + t.toString());
            }
        }

        // Print objects
        TreeIterator<Notifier> iter = psmJqlExtractResourceSet.getAllContents();
        while (iter.hasNext()) {
            final Notifier obj = iter.next();
            log.debug(obj.toString());
        }

        // Saving the extract model
        PsmJqlExtractModelLoader.savePsmJqlExtractModel(jqlExtractModel);
    }


    public File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }
}
