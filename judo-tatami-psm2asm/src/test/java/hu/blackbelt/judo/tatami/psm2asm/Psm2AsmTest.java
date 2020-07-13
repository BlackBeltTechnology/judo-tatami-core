package hu.blackbelt.judo.tatami.psm2asm;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmUtils.isAllowedToCreateEmbeddedObject;
import static hu.blackbelt.judo.meta.asm.runtime.AsmUtils.isAllowedToDeleteEmbeddedObject;
import static hu.blackbelt.judo.meta.asm.runtime.AsmUtils.isAllowedToUpdateEmbeddedObject;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace.fromModelsAndTrace;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Psm2AsmTest {

    public static final String DEMO = "demo";
    public static final String NORTHWIND_PSM_MODEL = "northwind-psm.model";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String NORTHWIND_PSM_2_ASM_MODEL = "northwind-psm2asm.model";

    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        psmModel = new Demo().fullDemo();

        // When model is invalid the loader have to throw exception. This checks that invalid model cannot valid -if
        // the loading check does not run caused by some reason
        assertTrue(psmModel.isValid());

        validatePsm(new Slf4jLog(log), psmModel,
                calculatePsmValidationScriptURI());

        // Create empty ASM model
        asmModel = buildAsmModel()
                .name(DEMO)
                .build();
    }

    @Test
    public void testPsm2AsmTransformation() throws Exception {

        // Make transformation which returns the trace with the serialized URI's
        Psm2AsmTransformationTrace psm2AsmTransformationTrace = executePsm2AsmTransformation(psmModel, asmModel);

        psm2AsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_2_ASM_MODEL));

        Psm2AsmTransformationTrace psm2AsmTransformationTraceLoaded = fromModelsAndTrace(
                DEMO,
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

        AsmUtils asmUtils = new AsmUtils(asmModel.getResourceSet());
        Optional<EClass> orderInfo = asmUtils.getClassByFQName("demo.services.OrderInfo");
        assertTrue(orderInfo.isPresent());

        final Optional<EReference> itemsOfOrderInfo = orderInfo.get().getEAllReferences().stream().filter(r -> "items".equals(r.getName())).findAny();
        assertTrue(itemsOfOrderInfo.isPresent());

        assertTrue(isAllowedToCreateEmbeddedObject(itemsOfOrderInfo.get()));
        assertTrue(isAllowedToUpdateEmbeddedObject(itemsOfOrderInfo.get()));
        assertTrue(isAllowedToDeleteEmbeddedObject(itemsOfOrderInfo.get()));
    }

}