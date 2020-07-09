package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.calculateAsm2SDKTemplateScriptURI;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.executeAsm2SDKGeneration;

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
        slf4jlog = new Slf4jLog(log);


        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL).getAbsolutePath()))
                .name(NORTHWIND));
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
