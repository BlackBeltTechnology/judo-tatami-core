package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.executeAsm2JAXRSAPIGeneration;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteStreams;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2JAXRSAPITest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    
    Log slf4jlog;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        PsmModel psmModel = new Demo().fullDemo();

        // Create empty RDBMS model
        asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        executePsm2AsmTransformation(psmModel, asmModel);
    }

    @Test
    public void testExecuteAsm2JAXRSAPIGeneration() throws Exception {
        try (OutputStream bundleOutputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-rest.jar"))) {
            ByteStreams.copy(
                    executeAsm2JAXRSAPIGeneration(asmModel,
                            new File(TARGET_TEST_CLASSES, "generated/java")),
                    bundleOutputStream
            );
        }
    }

}
