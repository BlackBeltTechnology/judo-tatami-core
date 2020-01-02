package hu.blackbelt.judo.tatami.workflow.osgi;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This configuration represents with default header names which contains the Script Root's URI names
 */
@ObjectClassDefinition(name="PSM Transformation Workflow Process configurations")
public @interface PsmWorkflowProcessConfiguration {
    @AttributeDefinition
    String sqlDialect() default "hsqldb";
}
