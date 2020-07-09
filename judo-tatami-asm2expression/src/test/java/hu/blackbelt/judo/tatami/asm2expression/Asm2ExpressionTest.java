package hu.blackbelt.judo.tatami.asm2expression;

import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.buildExpressionModel;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.SaveArguments.expressionSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2ExpressionTest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String NORTHWIND_MEASURE_MODEL = "northwind-measure.model";
    public static final String NORTHWIND_EXPRESSION_MODEL = "northwind-expression.model";

    Log slf4jlog;
    AsmModel asmModel;
    MeasureModel measureModel;
    ExpressionModel expressionModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);
        
        PsmModel psmModel = new Demo().fullDemo();

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
        expressionModel = buildExpressionModel()
                .name(NORTHWIND)
                .build();

    }

    @Test
    public void testExecuteAsm2ExpressionGeneration() throws Exception {
        Asm2Expression.executeAsm2Expression(asmModel, measureModel, expressionModel);

        expressionModel.saveExpressionModel(expressionSaveArgumentsBuilder()
                .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND_EXPRESSION_MODEL))));

    }
}
