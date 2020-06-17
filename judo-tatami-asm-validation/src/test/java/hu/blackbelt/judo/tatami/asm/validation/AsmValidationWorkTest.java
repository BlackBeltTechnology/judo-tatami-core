package hu.blackbelt.judo.tatami.asm.validation;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
class AsmValidationWorkTest {

	public static final String NORTHWIND = "northwind";

    TransformationContext transformationContext;
    AsmValidationWork asmValidationWork;

	@BeforeEach
	void setUp() throws Exception {

		Demo demo = new Demo();
		PsmModel psmModel = demo.fullDemo();

		AsmModel asmModel = buildAsmModel().name(NORTHWIND).build();
		executePsm2AsmTransformation(psmModel, asmModel);

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(asmModel);

		asmValidationWork = new AsmValidationWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(asmValidationWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		if (workReport.getError() != null) {
			log.error("Error found:", workReport.getError());
		}

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}

}