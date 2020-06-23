package hu.blackbelt.judo.tatami.psm2measure;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.List;

import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.SaveArguments.measureSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.buildMeasureModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.*;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace.fromModelsAndTrace;

@Slf4j
public class Psm2MeasureTest {
    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_PSM_MODEL = "northwind-psm.model";
    public static final String NORTHWIND_MEASURE_MODEL = "northwind-measure.model";
    public final static String NORTHWIND_PSM_2_MEASURE_MODEL = "northwind-psm2measure.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    PsmModel psmModel;
    MeasureModel measureModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = loadPsmModel(psmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL))
                .name(NORTHWIND));

        // Create empty MEASURE model
        measureModel = buildMeasureModel()
                .name(NORTHWIND)
                .build();
    }


    @Test
    public void testPsm2MeasureTransformation() throws Exception {
        Psm2MeasureTransformationTrace psm2MeasureTransformationTrace =
                executePsm2MeasureTransformation(psmModel, measureModel);

        // Saving trace map
        psm2MeasureTransformationTrace.save(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_2_MEASURE_MODEL));

        // Loading trace map
        Psm2MeasureTransformationTrace psm2MeasureTransformationTraceLoaded =
                fromModelsAndTrace(NORTHWIND, psmModel, measureModel, new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_2_MEASURE_MODEL));

        Map<EObject, List<EObject>> resolvedTrace = psm2MeasureTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.info(e.toString() + " -> " + t.toString());
            }
        }
        measureModel.saveMeasureModel(measureSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_MEASURE_MODEL)));
    }


}