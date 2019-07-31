package hu.blackbelt.judo.tatami.psm2measure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.measure.support.MeasureModelResourceSupport;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
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
import java.util.Map;
import java.util.List;
import java.util.Optional;

import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.*;

@Slf4j
public class Psm2MeasureTest {

    public final static String PSM_2_MEASURE_MODEL = "psm2measure.model";
    public static final String TRACE_PSM_2_MEASURE = "trace:psm2measure";
    public static final String MEASURE_NORTHWIND = "measure:northwind";
    public static final String PSM_NORTHWIND = "psm:northwind";
    public static final String URN_NORTHWIND_PSM_MODEL = "urn:northwind-psm.model";
    public static final String URN_NORTHWIND_MEASURE = "urn:northwind-measure.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND = "northwind";

    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;
    MeasureModel measureModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), TARGET_TEST_CLASSES)),
                ImmutableMap.of(
                        URI.createURI(PSM_NORTHWIND), URI.createURI(URN_NORTHWIND_PSM_MODEL),
                        URI.createURI(MEASURE_NORTHWIND), URI.createURI(URN_NORTHWIND_MEASURE))
        );

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = PsmModel.loadPsmModel(PsmModel.LoadArguments.loadArgumentsBuilder()
                .uri(URI.createURI(PSM_NORTHWIND))
                .uriHandler(Optional.of(uriHandler))
                .name(NORTHWIND)
                .build());

        // Create empty MEASURE model
        MeasureModelResourceSupport measureModelResourceSupport = MeasureModelResourceSupport.measureModelResourceSupportBuilder()
                .uriHandler(Optional.of(uriHandler))
                .build();

        measureModel = MeasureModel.buildMeasureModel()
                .measureModelResourceSupport(measureModelResourceSupport)
                .name(NORTHWIND)
                .uri(URI.createURI(PSM_NORTHWIND))
                .build();
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testPsm2MeasureTransformation() throws Exception {
        Psm2MeasureTransformationTrace psm2MeasureTransformationTrace = executePsm2MeasureTransformation(measureModel.getResourceSet(), psmModel, measureModel, new Slf4jLog(log),
                new File(TARGET_TEST_CLASSES, "epsilon/transformations/measure").toURI());

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getPsm2MeasureTrace(psm2MeasureTransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(TARGET_TEST_CLASSES, PSM_2_MEASURE_MODEL)), ImmutableMap.of());

        // Loadeing trace map
        ResourceSet traceLoadedResourceSet = createPsm2MeasureTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_PSM_2_MEASURE));
        traceResoureLoaded.load(new FileInputStream(new File(TARGET_TEST_CLASSES, PSM_2_MEASURE_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolvePsm2MeasureTrace(traceResoureLoaded, psmModel, measureModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.info(e.toString() + " -> " + t.toString());
            }
        }
        measureModel.saveMeasureModel();
    }


}