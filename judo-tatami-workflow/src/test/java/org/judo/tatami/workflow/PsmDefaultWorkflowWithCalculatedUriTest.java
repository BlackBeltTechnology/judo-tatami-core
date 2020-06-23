package org.judo.tatami.workflow;

import com.google.common.collect.ImmutableList;
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
import hu.blackbelt.model.northwind.Demo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSave.saveModels;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PsmDefaultWorkflowWithCalculatedUriTest {
	
	PsmDefaultWorkflow defaultWorkflow;


    private WorkReport workReport;
    

	@Test
	void testDefaultWorkflowWithCalculatedUri() throws URISyntaxException, IOException, PsmValidationException {
		defaultWorkflow = new PsmDefaultWorkflow(
						DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
						.psmModel(new Demo().fullDemo())
						.modelName("northwind")
						.dialectList(ImmutableList.of("hsqldb", "oracle")));
		workReport = defaultWorkflow.startDefaultWorkflow();
		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}