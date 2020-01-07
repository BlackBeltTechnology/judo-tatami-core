package hu.blackbelt.judo.tatami.workflow.osgi;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;

public interface TransformationContextRegistrationService {

    void registerTramsformationContext(TransformationContext transformationContext, String sqlDialect);

    void unregisterTramsformationContext(TransformationContext transformationContext, String sqlDialect);

}
