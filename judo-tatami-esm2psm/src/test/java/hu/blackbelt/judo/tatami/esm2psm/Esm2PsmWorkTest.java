package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.*;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;

class Esm2PsmWorkTest {

	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ESM_MODEL = "northwind-esm.model";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";

	Esm2PsmWork esm2PsmWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, EsmModel.EsmValidationException, URISyntaxException {
		EsmModel esmModel = EsmModel.loadEsmModel(
				esmLoadArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_MODEL)).name(NORTHWIND));

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(esmModel);

		esm2PsmWork = new Esm2PsmWork(transformationContext, calculateEsm2PsmTransformationScriptURI());
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(esm2PsmWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}

}
