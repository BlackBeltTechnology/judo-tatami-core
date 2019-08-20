package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.*;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace.fromModelsAndTrace;
import static org.junit.Assert.assertTrue;

@Slf4j
public class Psm2AsmTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_PSM_MODEL = "northwind-psm.model";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String NORTHWIND_PSM_2_ASM_MODEL = "northwind-psm2asm.model";

    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = loadPsmModel(psmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL))
                .name(NORTHWIND));

        // When model is invalid the loader have to throw exception. This checks that invalid model cannot valid -if
        // the loading check does not run caused by some reason
        assertTrue(psmModel.isValid());

        validatePsm(new Slf4jLog(log), psmModel,
                UriUtil.resolve(".",
                        PsmModel.class.getClassLoader().getResource("validations/psm.evl").toURI()));

        // Create empty ASM model
        asmModel = buildAsmModel()
                .name(NORTHWIND)
                .build();

    }

    @Test
    public void testPsm2AsmTransformation() throws Exception {

        // Make transformation which returns the trace with the serialized URI's
        Psm2AsmTransformationTrace psm2AsmTransformationTrace = executePsm2AsmTransformation(
                psmModel,
                asmModel,
                new Slf4jLog(log),
                new File("src/main/epsilon/transformations/asm").toURI());

        psm2AsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_2_ASM_MODEL));

        Psm2AsmTransformationTrace psm2AsmTransformationTraceLoaded = fromModelsAndTrace(
                NORTHWIND,
                psmModel,
                asmModel,
                new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_2_ASM_MODEL));

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = psm2AsmTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                        .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL))));
    }


}