package hu.blackbelt.judo.tatami.psm.validation;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.psm.PsmEpsilonValidator;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PsmValidationWork extends AbstractTransformationWork {

    public PsmValidationWork(TransformationContext transformationContext) {
        super(transformationContext);
    }

    @Override
    public void execute() throws Exception {
        Optional<PsmModel> esmModel = getTransformationContext().getByClass(PsmModel.class);
        esmModel.orElseThrow(() -> new IllegalArgumentException("PSM Model does not found in transformation context"));

        PsmEpsilonValidator.validatePsm(new Slf4jLog(log), esmModel.get(), PsmEpsilonValidator.calculatePsmValidationScriptURI());
    }
}
