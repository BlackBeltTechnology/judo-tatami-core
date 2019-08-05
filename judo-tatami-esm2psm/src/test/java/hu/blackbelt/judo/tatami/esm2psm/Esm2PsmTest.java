package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.support.PsmModelResourceSupport;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.loadEsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.support.PsmModelResourceSupport.psmModelResourceSupportBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.*;

@Slf4j
public class Esm2PsmTest {

    public static final String ESM_2_PSM_MODEL = "esm2psm.model";
    public static final String TRACE_ESM_2_PSM = "esm2esm:northwind";
    public static final String NORTHWIND_ESM_MODEL = "northwind-demo-esm.model";
    public static final String NORTHWIND_PSM_MODEL = "northwind-psm.model";
    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;
    PsmModel psmModel;

    @Before
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = loadEsmModel(esmLoadArgumentsBuilder()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_MODEL).getAbsolutePath()))
                .name(NORTHWIND));

        // Create empty PSM model
        psmModel = buildPsmModel()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL).getAbsolutePath()))
                .name(NORTHWIND)
                .build();

    }

    @Test
    public void testEsm2PsmTransformation() throws Exception {

        // Make transformation which returns the tracr with the serialized URI's
        Esm2PsmTransformationTrace esm2PsmTransformationTrace = executeEsm2PsmTransformation(
                esmModel,
                psmModel,
                new Slf4jLog(log),
                new File("target/test-classes/epsilon/transformations/psm").toURI());

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getEsm2PsmTrace(esm2PsmTransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(TARGET_TEST_CLASSES, ESM_2_PSM_MODEL)), ImmutableMap.of());

        // Loadeing trace map
        ResourceSet traceLoadedResourceSet = createEsm2PsmTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_ESM_2_PSM));
        traceResoureLoaded.load(new FileInputStream(new File(TARGET_TEST_CLASSES, ESM_2_PSM_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveEsm2PsmTrace(traceResoureLoaded, esmModel, psmModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }
        psmModel.savePsmModel();
    }
}
