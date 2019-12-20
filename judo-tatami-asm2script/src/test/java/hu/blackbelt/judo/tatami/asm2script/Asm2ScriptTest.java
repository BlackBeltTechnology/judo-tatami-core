package hu.blackbelt.judo.tatami.asm2script;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.SaveArguments.expressionSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.buildExpressionModel;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.LoadArguments.measureLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.loadMeasureModel;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.SaveArguments.scriptSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.buildScriptModel;

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

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL).getAbsolutePath()))
                .name(NORTHWIND));

        measureModel = loadMeasureModel(measureLoadArgumentsBuilder()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_MEASURE_MODEL).getAbsolutePath()))
                .name(NORTHWIND));

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
