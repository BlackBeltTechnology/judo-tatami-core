package hu.blackbelt.judo.tatami.workflow.osgi;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationConstants.*;
import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationBundleTrackerHelper.*;

/**
 * This configuration respresents with default bundles all of the transformation budnles are usable with Tatami.
 */
@ObjectClassDefinition(name="PSM Transformation Workflow Process configurations")
public @interface PsmWorkflowProcessConfiguration {
    @AttributeDefinition
    String sqlDialect() default "hsqldb";

    @AttributeDefinition
    String rdbms2LiquibaseTransformationScriptUriHeaderName() default MANIFEST_TAG_RDBMS_TO_LIQUIBASE_SCRIPT_ROOT;

    @AttributeDefinition
    String psm2MeasureTransformationScriptUriHeaderName() default MANIFEST_TAG_PSM_2_MEASURE_SCRIPT_ROOT;

    @AttributeDefinition
    String psm2AsmTransformationScriptUriHeaderName() default MANIFEST_TAG_PSM_2_ASM_SCRIPT_ROOT;

    @AttributeDefinition
    String asm2RdbmsTransformationScriptUriHeaderName() default MANIFEST_TAG_ASM_2_RDBMS_SCRIPT_ROOT;

    @AttributeDefinition
    String asm2RdbmsTransformationModelUriHeaderName() default MANIFEST_TAG_ASM_2_RDBMS_MODEL_ROOT;

    @AttributeDefinition
    String asm2OpenapiTransformationScriptUriHeaderName() default MANIFEST_TAG_ASM_2_OPENAPI_SCRIPT_ROOT;

    @AttributeDefinition
    String asm2JaxrsapiTransformationScriptUriHeaderName() default MANIFEST_TAG_ASM_2_JAXRSAPI_SCRIPT_ROOT;

    @AttributeDefinition
    String asm2SdkTransformationScriptUriHeaderName() default MANIFEST_TAG_ASM_2_SDK_SCRIPT_ROOT;
}
