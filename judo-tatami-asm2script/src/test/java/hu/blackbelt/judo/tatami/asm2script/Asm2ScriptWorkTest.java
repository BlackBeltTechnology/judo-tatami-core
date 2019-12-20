package hu.blackbelt.judo.tatami.asm2script;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.tatami.asm2script.Asm2ScriptWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.loadMeasureModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class Asm2ScriptWorkTest {
	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";

	Asm2ScriptWork asm2ScriptWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, AsmModel.AsmValidationException, URISyntaxException, MeasureModel.MeasureValidationException {
		AsmModel asmModel = loadAsmModel(
				asmLoadArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL)).name(NORTHWIND));

		MeasureModel measureModel = loadMeasureModel(MeasureModel.LoadArguments.measureLoadArgumentsBuilder()
				.file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL)).name(NORTHWIND));

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(asmModel);
		transformationContext.put(measureModel);

		asm2ScriptWork = new Asm2ScriptWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(asm2ScriptWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
