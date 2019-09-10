package org.judo.tatami.workflow;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.FacetImpl;
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
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;

public class DefaultWorkflowTest {
	
	DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
	
    public static final URI ASM_URI = new File("../judo-tatami-psm2asm/src/main/epsilon/transformations/asm/").toURI();
    public static final URI MEASURE_URI = new File("../judo-tatami-psm2measure/src/main/epsilon/transformations/measure/").toURI();
    public static final URI RDBMS_URI = new File("../judo-tatami-asm2rdbms/src/main/epsilon/transformations/").toURI();
    public static final URI OPENAPI_URI = new File("../judo-tatami-asm2openapi/src/main/epsilon/transformations/openapi/").toURI();
    public static final URI LIQUIBASE_URI = new File("../judo-tatami-rdbms2liquibase/src/main/epsilon/transformations/").toURI();
    public static final URI EXCELMODEL_URI = new File("../judo-tatami-asm2rdbms/model/").toURI();
    
    public static final String FILE_LOCATION = "target/test-classes/northwind-psm.model";
    
    public static final File TARGET_TEST_CLASSES = new File("target/test-classes");

    public static final String TARGET_CLASSES = "target/test-classes";
    
    public static final String MODEL_NAME = "northwind";
    public static final String DIALECT = "hsqldb";

    private WorkReport workReport;
    
    private File asmModel;
    private File measureModel;
    private File rdbmsModel;
    private File openapiModel;
    private File liquibaseModel;
    private File psm2asmTransformationTrace;
    private File psm2measureTransformationTrace;
    private File asm2rdbmsTransformationTrace;
    private File asm2openapiTransformationTrace;
    
	@BeforeEach
	void setUp() throws IOException, PsmValidationException, URISyntaxException {
		
		asmModel = new File(TARGET_CLASSES, "asm.model");
		asmModel.delete();
		measureModel = new File(TARGET_CLASSES, "measure.model");
		measureModel.delete();
		rdbmsModel = new File(TARGET_CLASSES, "rdbms.model");
		rdbmsModel.delete();
		openapiModel = new File(TARGET_CLASSES, "openapi.model");
		openapiModel.delete();
		liquibaseModel = new File(TARGET_CLASSES, "liquibase.changelog.xml");
		liquibaseModel.delete();
		psm2asmTransformationTrace = new File(TARGET_CLASSES, "psm2asm.transformationtrace");
		psm2asmTransformationTrace.delete();
		psm2measureTransformationTrace = new File(TARGET_CLASSES, "psm2measure.transformationtrace");
		psm2measureTransformationTrace.delete();
		asm2rdbmsTransformationTrace = new File(TARGET_CLASSES, "asm2rdbms.transformationtrace");
		asm2rdbmsTransformationTrace.delete();
		asm2openapiTransformationTrace = new File(TARGET_CLASSES, "asm2openapi.transformationtrace");
		asm2openapiTransformationTrace.delete();
		
		defaultWorkflow.setUp(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
				.psmModeldest(new File(FILE_LOCATION))
				.asmModelURI(ASM_URI)
				.measureModelURI(MEASURE_URI)
				.rdbmsModelURI(RDBMS_URI)
				.openapiModelURI(OPENAPI_URI)
				.liquibaseModelURI(LIQUIBASE_URI)
				.modelName(MODEL_NAME)
				.dialect(DIALECT)
				.excelModelURI(EXCELMODEL_URI));
		workReport = defaultWorkflow.startDefaultWorkflow();
	}
	
	@Test
	void testDefaultWorkflow() 
			throws IOException,
				   AsmValidationException,
				   MeasureValidationException,
				   RdbmsValidationException,
				   OpenapiValidationException,
				   LiquibaseValidationException {
		
		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
		defaultWorkflow.saveModels(TARGET_TEST_CLASSES);
		assertTrue(asmModel.exists());
		assertTrue(measureModel.exists());
		assertTrue(rdbmsModel.exists());
		assertTrue(openapiModel.exists());
		assertTrue(liquibaseModel.exists());
		
		assertTrue(psm2asmTransformationTrace.exists());
		assertTrue(psm2measureTransformationTrace.exists());
		assertTrue(asm2rdbmsTransformationTrace.exists());
		assertTrue(asm2openapiTransformationTrace.exists());
		
	}
}