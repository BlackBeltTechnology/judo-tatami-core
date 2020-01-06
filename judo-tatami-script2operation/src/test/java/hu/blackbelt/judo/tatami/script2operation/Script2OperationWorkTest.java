package hu.blackbelt.judo.tatami.script2operation;

import java.io.File;
import java.io.IOException;

import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.LoadArguments.scriptLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.loadScriptModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class Script2OperationWorkTest {
	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";

	Script2OperationWork script2OperationWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, ScriptModel.ScriptValidationException {
		ScriptModel scriptModel = loadScriptModel(
				scriptLoadArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL)).name(NORTHWIND));

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(scriptModel);

		script2OperationWork = new Script2OperationWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(script2OperationWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
