package hu.blackbelt.judo.tatami.esm.validation;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
class EsmValidationWorkTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_ESM_MODEL = "northwind-esm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    TransformationContext transformationContext;
    EsmValidationWork esmValidationWork;

	@BeforeEach
	void setUp() throws IOException, EsmModel.EsmValidationException, URISyntaxException {
		EsmModel esmModel = EsmModel.loadEsmModel(
				EsmModel.LoadArguments.esmLoadArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_MODEL)).name(NORTHWIND));

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(esmModel);

		esmValidationWork = new EsmValidationWork(transformationContext);
	}

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