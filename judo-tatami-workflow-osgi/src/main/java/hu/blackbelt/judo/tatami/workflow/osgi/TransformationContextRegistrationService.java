package hu.blackbelt.judo.tatami.workflow.osgi;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;

public interface TransformationContextRegistrationService {

    void registerTransformationContext(TransformationContext transformationContext, String sqlDialect);

    void unregisterTransformationContext(TransformationContext transformationContext, String sqlDialect);

}
