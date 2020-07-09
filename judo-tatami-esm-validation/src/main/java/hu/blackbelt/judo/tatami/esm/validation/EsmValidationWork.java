package hu.blackbelt.judo.tatami.esm.validation;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class EsmValidationWork extends AbstractTransformationWork {

    public EsmValidationWork(TransformationContext transformationContext) {
        super(transformationContext);
    }

    @Override
    public void execute() throws Exception {
        Optional<EsmModel> esmModel = getTransformationContext().getByClass(EsmModel.class);
        esmModel.orElseThrow(() -> new IllegalArgumentException("ESM Model does not found in transformation context"));

        EsmEpsilonValidator.validateEsm(new Slf4jLog(log), esmModel.get(), EsmEpsilonValidator.calculateEsmValidationScriptURI());
    }
}
