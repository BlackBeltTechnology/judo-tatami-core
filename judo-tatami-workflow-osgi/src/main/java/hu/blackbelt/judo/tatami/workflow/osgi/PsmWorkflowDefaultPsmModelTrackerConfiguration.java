package hu.blackbelt.judo.tatami.workflow.osgi;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="PSM Transformation Workflow Model Tracker configuration")
public @interface PsmWorkflowDefaultPsmModelTrackerConfiguration {

    @AttributeDefinition
    String transformationContextRegistrationServiceFilter() default "(implementation=default)";

    @AttributeDefinition
    String sqlDialect() default "hsqldb";

    @AttributeDefinition
    boolean ignorePsm2Asm() default false;

    @AttributeDefinition
    boolean ignorePsm2Measure() default false;

    @AttributeDefinition
    boolean ignoreAsm2Expression() default false;

    @AttributeDefinition
    boolean ignoreAsm2jaxrsapi() default false;

    @AttributeDefinition
    boolean ignoreAsm2Openapi() default false;

    @AttributeDefinition
    boolean ignoreAsm2Rdbms() default false;

    @AttributeDefinition
    boolean ignoreAsm2Script() default false;

    @AttributeDefinition
    boolean ignoreAsm2Sdk() default false;

    @AttributeDefinition
    boolean ignoreRdbms2Liquibase() default false;

    @AttributeDefinition
    boolean ignoreScript2Operation() default false;

    @AttributeDefinition
    boolean validateModels() default false;

    @AttributeDefinition
    boolean saveCompletedModels() default false;

    @AttributeDefinition
    boolean saveFailedModels() default false;

    @AttributeDefinition
    String outputDirectory() default "";

}
