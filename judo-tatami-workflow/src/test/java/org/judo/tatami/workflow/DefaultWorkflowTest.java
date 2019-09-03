package org.judo.tatami.workflow;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;

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

public class DefaultWorkflowTest {
	
	DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
	
    public static final URI ASM_URI = new File("src/main/epsilon/transformations/asm").toURI();
    public static final URI MEASURE_URI = new File("src/main/epsilon/transformations/measure").toURI();
    public static final URI RDBMS_URI = new File("src/main/epsilon/transformations/rdbms").toURI();
    public static final URI OPENAPI_URI = new File("src/main/epsilon/transformations/openapi").toURI();
    public static final URI LIQUIBASE_URI = new File("src/main/epsilon/transformations").toURI();
    
    public static final String FILE_LOCATION = "target/test-classes/northwind-psm.model";
    
    public static final URI TARGET_TEST_CLASSES = new File("target/test-classes").toURI();

    public static final String TARGET_CLASSES = "target/test-classes";

    private WorkReport workReport;
    
    private File asmModel;
    private File measureModel;
    private File rdbmsModel;
    private File openapiModel;
    private File liquibaseModel;
    
	@BeforeEach
	void setUp() throws IOException, PsmValidationException {
		
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
		
		defaultWorkflow.setUp(new File(FILE_LOCATION),ASM_URI,OPENAPI_URI,MEASURE_URI,RDBMS_URI,LIQUIBASE_URI);
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
		
	}
}