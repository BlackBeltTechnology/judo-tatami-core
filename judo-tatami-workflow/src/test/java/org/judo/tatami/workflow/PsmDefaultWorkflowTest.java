package org.judo.tatami.workflow;

import static hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSave.saveModels;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;

public class PsmDefaultWorkflowTest {
	
	PsmDefaultWorkflow defaultWorkflow = new PsmDefaultWorkflow();
	
    public static final URI PSM2ASM_SCRIPTROOT = new File("../judo-tatami-psm2asm/src/main/epsilon/transformations/asm/").toURI();
    public static final URI PSM2MEASURE_SCRIPTROOT = new File("../judo-tatami-psm2measure/src/main/epsilon/transformations/measure/").toURI();
    public static final URI ASM2RDBMS_SCRIPTROOT = new File("../judo-tatami-asm2rdbms/src/main/epsilon/transformations/").toURI();
    public static final URI ASM2OPENAPI_SCRIPTROOT = new File("../judo-tatami-asm2openapi/src/main/epsilon/transformations/openapi/").toURI();
    public static final URI RDMBS2LIQUIBASE_SCRIPTROOT = new File("../judo-tatami-rdbms2liquibase/src/main/epsilon/transformations/").toURI();
    public static final URI EXCELMODEL_SCRIPTROOT = new File("../judo-tatami-asm2rdbms/model/").toURI();
    public static final URI ASM2SDK_SCRIPTROOT = new File("../judo-tatami-asm2sdk/src/main/epsilon/templates/").toURI();
    public static final URI ASM2JAXRSAPI_SCRIPTROOT = new File("../judo-tatami-asm2jaxrsapi/src/main/epsilon/templates/").toURI();
    
    public static final String FILE_LOCATION = "target/test-classes/northwind-psm.model";
    
    public static final File TARGET_TEST_CLASSES = new File("target/test-classes");

    public static final String TARGET_CLASSES = "target/test-classes";
    
    public static final String MODEL_NAME = "northwind";
    public static final List<String> DIALECT_LIST = new ArrayList<>(Arrays.asList("hsqldb", "oracle"));

    private WorkReport workReport;
    
    private File asmModel;
    private File measureModel;
    private List<File> rdbmsModels = new ArrayList<>();
    private File openapiModel;
    private List<File> liquibaseModels = new ArrayList<>();
    private File psm2asmTransformationTrace;
    private File psm2measureTransformationTrace;
    private List<File> asm2rdbmsTransformationTraces = new ArrayList<>();
    private File asm2openapiTransformationTrace;
    private File asm2sdkBundle;
    private File asm2jaxrsapiBundle;

	@BeforeEach
	void setUp() throws IOException, PsmValidationException, URISyntaxException, AsmValidationException,
			MeasureValidationException, RdbmsValidationException, OpenapiValidationException,
			LiquibaseValidationException {

		asmModel = new File(TARGET_CLASSES, MODEL_NAME + "-asm.model");
		asmModel.delete();
		measureModel = new File(TARGET_CLASSES, MODEL_NAME + "-measure.model");
		measureModel.delete();
		DIALECT_LIST.forEach(dialect -> rdbmsModels.add(new File(TARGET_CLASSES, MODEL_NAME + "-rdbms_" + dialect + ".model")));
		rdbmsModels.forEach(rdbmsModel -> rdbmsModel.delete());
		openapiModel = new File(TARGET_CLASSES, MODEL_NAME + "-openapi.model");
		openapiModel.delete();
		DIALECT_LIST.forEach(dialect -> liquibaseModels.add(new File(TARGET_CLASSES, MODEL_NAME + "-liquibase_" + dialect + ".changelog.xml")));
		liquibaseModels.forEach(liquibaseModel -> liquibaseModel.delete());
		psm2asmTransformationTrace = new File(TARGET_CLASSES, MODEL_NAME + "-psm2asm.model");
		psm2asmTransformationTrace.delete();
		psm2measureTransformationTrace = new File(TARGET_CLASSES, MODEL_NAME + "-psm2measure.model");
		psm2measureTransformationTrace.delete();
		DIALECT_LIST.forEach(dialect -> asm2rdbmsTransformationTraces.add(new File(TARGET_CLASSES, MODEL_NAME + "-asm2rdbms_" + dialect + ".model")));
		asm2rdbmsTransformationTraces.forEach(asm2rdbmsTransformationTrace -> asm2rdbmsTransformationTrace.delete());
		asm2openapiTransformationTrace = new File(TARGET_CLASSES, MODEL_NAME + "-asm2openapi.model");
		asm2openapiTransformationTrace.delete();
		asm2sdkBundle = new File(TARGET_CLASSES, MODEL_NAME + "-asm2sdk.jar");
		asm2sdkBundle.delete();
		asm2jaxrsapiBundle = new File(TARGET_CLASSES, MODEL_NAME + "-asm2jaxrsapi.jar");
		asm2jaxrsapiBundle.delete();

		defaultWorkflow.setUp(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
				.psmModelSourceURI(new File(FILE_LOCATION).toURI())
				.psm2AsmModelTransformationScriptURI(PSM2ASM_SCRIPTROOT)
				.psm2MeasureModelTransformationScriptURI(PSM2MEASURE_SCRIPTROOT)
				.asm2RdbmsModelTransformationScriptURI(ASM2RDBMS_SCRIPTROOT)
				.asm2OpenapiModelTransformationScriptURI(ASM2OPENAPI_SCRIPTROOT)
				.rdbms2LiquibaseModelTransformationScriptURI(RDMBS2LIQUIBASE_SCRIPTROOT)
				.modelName(MODEL_NAME)
				.dialectList(DIALECT_LIST)
				.asm2RdbmsModelTransformationModelURI(EXCELMODEL_SCRIPTROOT)
				.asm2sdkModelTransformationScriptURI(ASM2SDK_SCRIPTROOT)
				.asm2jaxrsapiModelTransformationScriptURI(ASM2JAXRSAPI_SCRIPTROOT));
		workReport = defaultWorkflow.startDefaultWorkflow();
		saveModels(defaultWorkflow.getTransformationContext(), TARGET_TEST_CLASSES, DIALECT_LIST);
	}

	@Test
	void testDefaultWorkflow() throws IOException, AsmValidationException, MeasureValidationException,
			RdbmsValidationException, OpenapiValidationException, LiquibaseValidationException {

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
		
		assertTrue(asmModel.exists());
		assertTrue(measureModel.exists());
		rdbmsModels.forEach(rdbmsModel -> assertTrue(rdbmsModel.exists()));
		assertTrue(openapiModel.exists());
		liquibaseModels.forEach(liquibaseModel -> assertTrue(liquibaseModel.exists()));

		assertTrue(psm2asmTransformationTrace.exists());
		assertTrue(psm2measureTransformationTrace.exists());
		asm2rdbmsTransformationTraces
				.forEach(asm2rdbmsTransformationTrace -> assertTrue(asm2rdbmsTransformationTrace.exists()));
		assertTrue(asm2openapiTransformationTrace.exists());
		
		assertTrue(asm2sdkBundle.exists());
		assertTrue(asm2jaxrsapiBundle.exists());

	}
}