package hu.blackbelt.judo.tatami.asm2sdk;

import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.executeAsm2SDKGeneration;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
public class Asm2SDKTest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String GENERATED_JAVA = "generated/java";

    Log slf4jlog;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {
    	 // Default logger
        final Log slf4jlog = new Slf4jLog(log);

        final PsmModel psmModel = new Demo().fullDemo();

        // Create empty ASM model
        asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        executePsm2AsmTransformation(psmModel, asmModel);
    }

    @Test
    public void testExecuteAsm2SDKGeneration() throws Exception {
        InputStream stream = executeAsm2SDKGeneration(asmModel, new File(TARGET_TEST_CLASSES, GENERATED_JAVA));
        try (OutputStream outputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-sdk.jar"))) {
            ByteStreams.copy(stream
                    ,
                    outputStream
            );
        }
        stream.close();

        try (OutputStream outputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-sdk.2.jar"))) {
            ByteStreams.copy(stream, outputStream);
        }
        stream.close();

    }
}
