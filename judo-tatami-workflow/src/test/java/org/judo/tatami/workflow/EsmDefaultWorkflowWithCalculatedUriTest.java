package org.judo.tatami.workflow;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.EsmDefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;
import hu.blackbelt.model.northwind.Demo;
import hu.blackbelt.model.northwind.esm.NorthwindEsmModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EsmDefaultWorkflowWithCalculatedUriTest {
	
	EsmDefaultWorkflow defaultWorkflow;


    private WorkReport workReport;
    

	@Test
	void testDefaultWorkflowWithCalculatedUri() throws URISyntaxException, IOException, EsmModel.EsmValidationException, ScriptExecutionException {
		defaultWorkflow = new EsmDefaultWorkflow(
						DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
						.esmModel(NorthwindEsmModel.fullDemo())
						.modelName("northwind")
						.dialectList(ImmutableList.of("hsqldb", "oracle")));
		workReport = defaultWorkflow.startDefaultWorkflow();
		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}