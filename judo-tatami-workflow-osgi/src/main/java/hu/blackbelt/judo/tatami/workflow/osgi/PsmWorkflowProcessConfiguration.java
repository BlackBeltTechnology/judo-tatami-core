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
    String rdbms2LiquibaseTransformationScriptUriHeaderName() default TRAANSFORMATION_RDBMS_TO_LIQUIBASE + SCRIPT_ROOT_POSTFIX;

    @AttributeDefinition
    String psm2MeasureTransformationScriptUriHeaderName() default TRAANSFORMATION_PSM_2_MEASURE + SCRIPT_ROOT_POSTFIX;

    @AttributeDefinition
    String psm2AsmTransformationScriptUriHeaderName() default TRAANSFORMATION_PSM_2_ASM + SCRIPT_ROOT_POSTFIX;

    @AttributeDefinition
    String asm2RdbmsTransformationScriptUriHeaderName() default TRAANSFORMATION_ASM_2_RDBMS + SCRIPT_ROOT_POSTFIX;

    @AttributeDefinition
    String asm2RdbmsTransformationModelUriHeaderName() default "RdbmsExcelModelURI";

    @AttributeDefinition
    String asm2OpenapiTransformationScriptUriHeaderName() default TRAANSFORMATION_ASM_2_OPENAPI + SCRIPT_ROOT_POSTFIX;

    @AttributeDefinition
    String asm2JaxrsapiTransformationScriptUriHeaderName() default TRAANSFORMATION_ASM_2_JAXRSAPI+ SCRIPT_ROOT_POSTFIX;

    @AttributeDefinition
    String asm2SdkTransformationScriptUriHeaderName() default TRAANSFORMATION_ASM_2_SDK + SCRIPT_ROOT_POSTFIX;
}
