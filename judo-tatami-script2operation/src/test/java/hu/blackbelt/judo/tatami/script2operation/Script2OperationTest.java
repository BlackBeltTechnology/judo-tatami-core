package hu.blackbelt.judo.tatami.script2operation;

import com.google.common.io.ByteStreams;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.LoadArguments.scriptLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.loadScriptModel;
import static hu.blackbelt.judo.tatami.script2operation.Script2Operation.executeScript2OperationGeneration;

@Slf4j
public class Script2OperationTest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND_SCRIPT_MODEL = "northwind-script.model";
    public static final String GENERATED_JAVA = "generated/java";

    ScriptModel scriptModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        scriptModel = loadScriptModel(scriptLoadArgumentsBuilder()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_SCRIPT_MODEL).getAbsolutePath()))
                .name(NORTHWIND));
    }

    @Test
    public void testExecuteAsm2SDKGeneration() throws Exception {
        try (OutputStream outputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-script2operation.jar"))) {
            ByteStreams.copy(
                    executeScript2OperationGeneration(scriptModel,
                            new File(TARGET_TEST_CLASSES, GENERATED_JAVA)), outputStream);
        }
    }
}