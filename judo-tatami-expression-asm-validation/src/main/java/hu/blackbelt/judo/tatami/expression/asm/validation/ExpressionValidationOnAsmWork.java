package hu.blackbelt.judo.tatami.expression.asm.validation;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.adapters.asm.ExpressionEpsilonValidatorOnAsm;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ExpressionValidationOnAsmWork extends AbstractTransformationWork {

    public ExpressionValidationOnAsmWork(TransformationContext transformationContext) {
        super(transformationContext);
    }

    @Override
    public void execute() throws Exception {

        Optional<ExpressionModel> expressionModel = getTransformationContext().getByClass(ExpressionModel.class);
        expressionModel.orElseThrow(() -> new IllegalArgumentException("Expression Model does not found in transformation context"));

        Optional<MeasureModel> measureModel = getTransformationContext().getByClass(MeasureModel.class);
        measureModel.orElseThrow(() -> new IllegalArgumentException("Measure Model does not found in transformation context"));

        Optional<AsmModel> asmModel = getTransformationContext().getByClass(AsmModel.class);
        asmModel.orElseThrow(() -> new IllegalArgumentException("ASM Model does not found in transformation context"));

        ExpressionEpsilonValidatorOnAsm.validateExpressionOnAsm(new Slf4jLog(log),
                asmModel.get(),
                measureModel.get(),
                expressionModel.get(),
                ExpressionEpsilonValidatorOnAsm.calculateExpressionValidationScriptURI());
    }
}
