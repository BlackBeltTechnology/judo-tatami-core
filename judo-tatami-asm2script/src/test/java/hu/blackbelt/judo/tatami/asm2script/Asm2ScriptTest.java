package hu.blackbelt.judo.tatami.asm2script;

import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.buildScriptModel;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.SaveArguments.scriptSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2ScriptTest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String NORTHWIND_MEASURE_MODEL = "northwind-measure.model";
    public static final String NORTHWIND_SCRIPT_MODEL = "northwind-script.model";

    Log slf4jlog;
    AsmModel asmModel;
    MeasureModel measureModel;
    ScriptModel scriptModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        final PsmModel psmModel = new Demo().fullDemo();

        // Create empty ASM model
        asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        // Create empty Measure model
        measureModel = MeasureModel.buildMeasureModel()
                .name(NORTHWIND)
                .build();
        
        executePsm2AsmTransformation(psmModel, asmModel);
        executePsm2MeasureTransformation(psmModel, measureModel);

        // Create empty Expression model
        scriptModel = buildScriptModel()
                .name(NORTHWIND)
                .build();
    }

    @Test
    public void testExecuteAsm2ExpressionGeneration() throws Exception {
        Asm2Script.executeAsm2Script(asmModel, measureModel, scriptModel);

        scriptModel.saveScriptModel(scriptSaveArgumentsBuilder()
                .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND_SCRIPT_MODEL))));

    }
}
