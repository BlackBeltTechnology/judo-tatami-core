package hu.blackbelt.judo.tatami.script2operation;

import com.google.common.io.ByteStreams;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.LoadArguments.scriptLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.loadScriptModel;
import static hu.blackbelt.judo.tatami.asm2script.Asm2Script.executeAsm2Script;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;
import static hu.blackbelt.judo.tatami.script2operation.Script2Operation.executeScript2OperationGeneration;

@Slf4j
public class Script2OperationTest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND_SCRIPT_MODEL = "northwind-script.model";

    Log slf4jlog;
    ScriptModel scriptModel;

    @BeforeEach
    public void setUp() throws Exception {
    	slf4jlog = new Slf4jLog(log);

        final PsmModel psmModel = new Demo().fullDemo();

        // Create empty ASM model
        AsmModel asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        // Create empty Measure model
        MeasureModel measureModel = MeasureModel.buildMeasureModel()
                .name(NORTHWIND)
                .build();
        
        // Create empty Script model
        scriptModel = ScriptModel.buildScriptModel()
                .name(NORTHWIND)
                .build();
        
        executePsm2AsmTransformation(psmModel, asmModel);
        executePsm2MeasureTransformation(psmModel, measureModel);
        executeAsm2Script(asmModel, measureModel, scriptModel);
    }

    @Test
    public void testExecuteScript2OperationGeneration() throws Exception {
        try (OutputStream outputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-script2operation.jar"))) {
            ByteStreams.copy(
                    executeScript2OperationGeneration(scriptModel), outputStream);
        }
    }
}
