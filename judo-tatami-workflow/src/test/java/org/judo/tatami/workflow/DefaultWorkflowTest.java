package org.judo.tatami.workflow;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;

public class DefaultWorkflowTest {
	
	DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
	
	@BeforeEach
	void setUp() throws IOException, PsmValidationException {
		defaultWorkflow.setUp();
	}
	
	@Test
	void testDefaultWorkflow() {
		WorkReport workReport = defaultWorkflow.startDefaultWorkflow();
		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}