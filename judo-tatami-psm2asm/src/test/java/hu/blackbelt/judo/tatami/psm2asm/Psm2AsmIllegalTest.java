package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.common.util.UriUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class Psm2AsmIllegalTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_PSM_MODEL = "northwind-illegal-psm.model";
    public static final String NORTHWIND_ASM_MODEL = "northwind-illegal-asm.model";

    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = loadPsmModel(psmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL))
                .name(NORTHWIND));

        // TODO: Check it is valid or not as metamodel?
        assertTrue(psmModel.isValid());

        /*
        // TODO: Check semantic check is Okay?
        validatePsm(new Slf4jLog(log), psmModel,
                UriUtil.resolve(".", PsmModel.class.getClassLoader().getResource("validations/psm.evl").toURI()));
        */
        asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();
    }

    @Test
    public void testPsm2AsmTransformation() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        /*Psm2AsmTransformationTrace psm2AsmTransformationTrace = */
    	executePsm2AsmTransformation(
                psmModel,
                asmModel,
                new Slf4jLog(log),
                calculatePsm2AsmTransformationScriptURI());

        assertFalse(asmModel.isValid());

        AsmModel.AsmValidationException exception = assertThrows(AsmModel.AsmValidationException.class, () -> {
            asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                    .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL))));
        });

        assertThat(exception.getMessage(), containsString("Invalid model"));
    }


}