package hu.blackbelt.judo.tatami.expression.esm.validation;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.expression.adapters.esm.ExpressionEpsilonValidatorOnEsm;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static hu.blackbelt.judo.meta.expression.runtime.ExpressionEpsilonValidator.calculateExpressionValidationScriptURI;

@Slf4j
public class ExpressionValidationOnEsmWork extends AbstractTransformationWork {

    public ExpressionValidationOnEsmWork(TransformationContext transformationContext) {
        super(transformationContext);
    }

    @Override
    public void execute() throws Exception {

        Optional<ExpressionModel> expressionModel = getTransformationContext().getByClass(ExpressionModel.class);
        expressionModel.orElseThrow(() -> new IllegalArgumentException("Expression Model does not found in transformation context"));

        Optional<EsmModel> esmModel = getTransformationContext().getByClass(EsmModel.class);
        esmModel.orElseThrow(() -> new IllegalArgumentException("ESM Model does not found in transformation context"));

        ExpressionEpsilonValidatorOnEsm.validateExpressionOnEsm(new Slf4jLog(log),
                esmModel.get(),
                expressionModel.get(),
                calculateExpressionValidationScriptURI());
    }
}
