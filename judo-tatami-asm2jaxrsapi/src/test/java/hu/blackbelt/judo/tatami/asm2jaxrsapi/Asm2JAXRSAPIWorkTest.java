package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.calculateAsm2JaxrsapiTemplateScriptURI;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class Asm2JAXRSAPIWorkTest {

	public static final String NORTHWIND = "northwind";

	Asm2JAXRSAPIWork asm2jaxrsapiWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws Exception {
		NorthwindModelLoader northwindModelLoader = NorthwindModelLoader.createNorthwindModelLoader(NORTHWIND);
		AsmModel asmModel = northwindModelLoader.getAsmModel();

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(asmModel);

		asm2jaxrsapiWork = new Asm2JAXRSAPIWork(transformationContext, calculateAsm2JaxrsapiTemplateScriptURI());
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(asm2jaxrsapiWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
