package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
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
import java.net.URISyntaxException;
import java.util.Optional;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Esm2PsmWorkTest {

	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ESM_MODEL = NORTHWIND + "-esm.model";
	public static final String NORTHWIND_PSM_MODEL = NORTHWIND + "-psm.model";
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
	void testSimpleWorkflow() throws IOException, PsmModel.PsmValidationException {
		WorkFlow workflow = aNewSequentialFlow().execute(esm2PsmWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));

		Optional<PsmModel> psmModel = transformationContext.getByClass(PsmModel.class);
		assertTrue(psmModel.isPresent());
		psmModel.get().savePsmModel(psmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL)));
	}

}
