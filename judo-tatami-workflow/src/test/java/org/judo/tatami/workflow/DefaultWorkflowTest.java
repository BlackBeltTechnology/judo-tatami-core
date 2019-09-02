package org.judo.tatami.workflow;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;

public class DefaultWorkflowTest {
	
	DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
	
	void setUp() throws IOException, PsmValidationException {	
		
		defaultWorkflow.setUp(new File("src/main/epsilon/defaultworkflow").toURI());
		
	}
	
	@Test
	void testDefaultWorkflow() {
		
		defaultWorkflow.startDefaultWorkflow();
		assertThat(defaultWorkflow.getStatus(), equalTo(WorkStatus.COMPLETED));
		
	}
}