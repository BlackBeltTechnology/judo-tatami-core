package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.model.northwind.Demo;

public class Asm2JAXRSAPIWorkTest {

	public static final String NORTHWIND = "northwind";

	Asm2JAXRSAPIWork asm2jaxrsapiWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws Exception {
		
        PsmModel psmModel = new Demo().fullDemo();
        // Create empty RDBMS model
        AsmModel asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        executePsm2AsmTransformation(psmModel, asmModel);

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(asmModel);

		asm2jaxrsapiWork = new Asm2JAXRSAPIWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(asm2jaxrsapiWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
