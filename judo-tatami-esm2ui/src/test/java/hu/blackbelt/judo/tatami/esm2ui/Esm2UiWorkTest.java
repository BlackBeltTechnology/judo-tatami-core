package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.model.northwind.esm.NorthwindEsmModel;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("JNG-1406 Work in progress")
class Esm2UiWorkTest {

	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ESM_MODEL = NORTHWIND + "-esm.model";
	public static final String NORTHWIND_UI_MODEL = NORTHWIND + "-ui.model";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";

	Esm2UiWork esm2UiWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, EsmModel.EsmValidationException, URISyntaxException, ScriptExecutionException {
		EsmModel esmModel = NorthwindEsmModel.fullDemo();
		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(esmModel);

		esm2UiWork = new Esm2UiWork(transformationContext, calculateEsm2UiTransformationScriptURI());
	}

	@Test
	void testSimpleWorkflow() throws IOException, UiModel.UiValidationException {
		WorkFlow workflow = aNewSequentialFlow().execute(esm2UiWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));

		Optional<UiModel> uiModel = transformationContext.getByClass(UiModel.class);
		assertTrue(uiModel.isPresent());
		uiModel.get().saveUiModel(uiSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_UI_MODEL)));
	}

}
