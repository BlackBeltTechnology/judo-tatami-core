package org.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.EsmDefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSave.saveModels;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EsmDefaultWorkflowTest {
	
	EsmDefaultWorkflow defaultWorkflow;

    public static final String FILE_LOCATION = "target/test-classes/esm/northwind-esm.model";
    
    public static final File TARGET_TEST_CLASSES = new File("target/test-classes/esm");

    public static final String TARGET_CLASSES = "target/test-classes/esm";
    
    public static final String MODEL_NAME = "northwind";
    public static final List<String> DIALECT_LIST = new ArrayList<>(Arrays.asList("hsqldb", "oracle"));

    private WorkReport workReport;

	private File psmModel;
	private File uiModel;
	private File asmModel;
    private File measureModel;
    private List<File> rdbmsModels = new ArrayList<>();
    private File openapiModel;
    private File keycloakModel;
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

		psmModel = new File(TARGET_CLASSES, MODEL_NAME + "-psm.model");
		psmModel.delete();
		uiModel = new File(TARGET_CLASSES, MODEL_NAME + "-ui.model");
		uiModel.delete();

		asmModel = new File(TARGET_CLASSES, MODEL_NAME + "-asm.model");
		asmModel.delete();
		measureModel = new File(TARGET_CLASSES, MODEL_NAME + "-measure.model");
		measureModel.delete();
		DIALECT_LIST.forEach(dialect -> rdbmsModels.add(new File(TARGET_CLASSES, MODEL_NAME + "-rdbms_" + dialect + ".model")));
		rdbmsModels.forEach(rdbmsModel -> rdbmsModel.delete());
		openapiModel = new File(TARGET_CLASSES, MODEL_NAME + "-openapi.model");
		openapiModel.delete();
		keycloakModel = new File(TARGET_CLASSES, MODEL_NAME + "-keycloak.model");
		keycloakModel.delete();
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

		defaultWorkflow = new EsmDefaultWorkflow(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
				.esmModelSourceURI(new File(FILE_LOCATION).toURI())
				.modelName(MODEL_NAME)
				.dialectList(DIALECT_LIST));
		workReport = defaultWorkflow.startDefaultWorkflow();
		saveModels(defaultWorkflow.getTransformationContext(), TARGET_TEST_CLASSES, DIALECT_LIST);
	}

	@Test
	void testDefaultWorkflow() {

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));

		assertTrue(psmModel.exists());
		assertTrue(uiModel.exists());
		assertTrue(asmModel.exists());
		assertTrue(measureModel.exists());
		rdbmsModels.forEach(rdbmsModel -> assertTrue(rdbmsModel.exists()));
		assertTrue(openapiModel.exists());
		assertTrue(keycloakModel.exists());
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