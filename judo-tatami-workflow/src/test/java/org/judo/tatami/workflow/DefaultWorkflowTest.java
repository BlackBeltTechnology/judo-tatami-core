package org.judo.tatami.workflow;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
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
	
	@BeforeEach
	void setUp() throws IOException, PsmValidationException {
		defaultWorkflow.setUp(new File(FILE_LOCATION),ASM_URI,OPENAPI_URI,MEASURE_URI,RDBMS_URI,LIQUIBASE_URI);
	}
	
	@Test
	void testDefaultWorkflow() {
		WorkReport workReport = defaultWorkflow.startDefaultWorkflow();
		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}