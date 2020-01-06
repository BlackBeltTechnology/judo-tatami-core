package hu.blackbelt.judo.tatami.workflow.osgi;

public class TransformationConstants {
    public static final String BUNDLE_SYMBOLIC_NAME = "BundleSymbolicName";
    public static final String TRANSFORMATION_NAME = "Name";

    public static final String SEMI_COLON = ";";
    public static final String COMA = ",";
    public static final String EQ = "=";

    public static final String SCRIPT_ROOT_POSTFIX = "-ScriptRoot";
    public static final String MODEL_ROOT_POSTFIX = "-ModelRoot";
    public static final String SCRIPT_HEADER_NAME = "transformationScriptRootHeaderName";
    public static final String MODEL_HEADER_NAME = "modelRootHeaderName";

    public static final String TRAANSFORMATION_PSM_2_ASM = "Psm2AsmTransformation";
    public static final String TRAANSFORMATION_RDBMS_TO_LIQUIBASE = "Rdbms2LiquibaseTransformation";
    public static final String TRAANSFORMATION_PSM_2_MEASURE = "Psm2MeasureTransformation";
    public static final String TRAANSFORMATION_ESM_2_PSM = "Esm2PsmTransformation";
    public static final String TRAANSFORMATION_ASM_2_RDBMS = "Asm2RdbmsTransformation";
    public static final String TRAANSFORMATION_ASM_2_OPENAPI = "Asm2OpenapiTransformation";
    public static final String TRAANSFORMATION_ASM_2_JAXRSAPI = "Asm2JaxrsApiTransformation";
    public static final String TRAANSFORMATION_ASM_2_SDK = "Asm2SdkTransformation";

    public static final String MANIFEST_TAG_PSM_2_ASM_SCRIPT_ROOT = TRAANSFORMATION_PSM_2_ASM + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_RDBMS_TO_LIQUIBASE_SCRIPT_ROOT = TRAANSFORMATION_RDBMS_TO_LIQUIBASE + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_PSM_2_MEASURE_SCRIPT_ROOT = TRAANSFORMATION_PSM_2_MEASURE + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_ESM_2_PSM_SCRIPT_ROOT = TRAANSFORMATION_ESM_2_PSM + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_ASM_2_RDBMS_SCRIPT_ROOT = TRAANSFORMATION_ASM_2_RDBMS + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_ASM_2_RDBMS_MODEL_ROOT = TRAANSFORMATION_ASM_2_RDBMS + MODEL_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_ASM_2_OPENAPI_SCRIPT_ROOT = TRAANSFORMATION_ASM_2_OPENAPI + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_ASM_2_JAXRSAPI_SCRIPT_ROOT = TRAANSFORMATION_ASM_2_JAXRSAPI + SCRIPT_ROOT_POSTFIX;
    public static final String MANIFEST_TAG_ASM_2_SDK_SCRIPT_ROOT = TRAANSFORMATION_ASM_2_SDK + SCRIPT_ROOT_POSTFIX;

    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_RDBMS_2_LIQUIBASE = "hu.blackbelt.judo.tatami.judo-tatami-rdbms2liquibase";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_PSM_2_MEASURE = "hu.blackbelt.judo.tatami.judo-tatami-psm2measure";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_PSM_2_ASM = "hu.blackbelt.judo.tatami.judo-tatami-psm2asm";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_ESM_2_PSM = "hu.blackbelt.judo.tatami.judo-tatami-esm2psm";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_ASM_2_RDBMS = "hu.blackbelt.judo.tatami.judo-tatami-asm2rdbms";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_ASM_2_OPENAPI = "hu.blackbelt.judo.tatami.judo-tatami-asm2openapi";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_ASM_2_JAXRSAPI = "hu.blackbelt.judo.tatami.judo-tatami-asm2jaxrsapi";
    public static final String BUNDLE_SYMBOLIC_NAME_JUDO_TATAMI_ASM_2_SDK = "hu.blackbelt.judo.tatami.judo-tatami-asm2sdk";
}
