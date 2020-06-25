package hu.blackbelt.judo.tatami.esm.validation;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.model.northwind.esm.NorthwindEsmModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
class EsmValidationWorkTest {

    public static final String NORTHWIND = "northwind";

    TransformationContext transformationContext;
    EsmValidationWork esmValidationWork;

	@BeforeEach
	void setUp() throws IOException, EsmModel.EsmValidationException, URISyntaxException, ScriptExecutionException {

		EsmModel esmModel = NorthwindEsmModel.fullDemo();
		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(esmModel);

		esmValidationWork = new EsmValidationWork(transformationContext);
	}

	@Disabled
	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(esmValidationWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		if (workReport.getError() != null) {
			log.error("Error found:", workReport.getError());
		}

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}

}