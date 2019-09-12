package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class Psm2AsmWorkTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_PSM_MODEL = "northwind-psm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Psm2AsmWork psm2AsmWork;
    TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, PsmModel.PsmValidationException {
		PsmModel psmModel = loadPsmModel(
				psmLoadArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL)).name(NORTHWIND));

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(psmModel);

		psm2AsmWork = new Psm2AsmWork(transformationContext, new File("src/main/epsilon/transformations/asm").toURI());
	}

	@Test
	void testSimpleWokflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(psm2AsmWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}

}