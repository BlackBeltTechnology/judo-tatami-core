package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.loadEsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.*;

@Slf4j
public class Esm2PsmTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_ESM_MODEL = "northwind-demo-esm.model";
    public static final String NORTHWIND_PSM_MODEL = "northwind-psm.model";
    public static final String NORTHWIND_ESM_2_PSM_MODEL = "northwind-esm2psm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;
    PsmModel psmModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = loadEsmModel(esmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_MODEL))
                .name(NORTHWIND));

        // Create empty PSM model
        psmModel = buildPsmModel()
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
                calculateEsm2PsmTransformationScriptURI());

        // Saving trace map
        esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_2_PSM_MODEL));

        // Loadeing trace map
        Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace.fromModelsAndTrace(
                NORTHWIND, esmModel, psmModel, new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_2_PSM_MODEL));

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = esm2PsmTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }
        psmModel.savePsmModel(psmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL)));
    }
}
