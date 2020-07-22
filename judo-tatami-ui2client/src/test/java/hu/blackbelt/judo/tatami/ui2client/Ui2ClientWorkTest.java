package hu.blackbelt.judo.tatami.ui2client;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

public class Ui2ClientWorkTest {

	private final String TEST = "test";

	Ui2ClientWork ui2FlutterWork;
	TransformationContext transformationContext;
	UiModel uiModel;

	@BeforeEach
	void setUp() throws Exception {
		EsmModel esmModel = EsmModel.buildEsmModel().name(TEST).build();
		esmModel.addContent(SimpleOrderModel.createSimpleOrderModel());

		// Create empty UI model
        uiModel = UiModel.buildUiModel().name(TEST).build();

        executeEsm2UiTransformation(esmModel, "desktop", 12, uiModel);
		transformationContext = new TransformationContext(TEST);
		transformationContext.put(uiModel);

		ui2FlutterWork = new Ui2ClientWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(ui2FlutterWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
