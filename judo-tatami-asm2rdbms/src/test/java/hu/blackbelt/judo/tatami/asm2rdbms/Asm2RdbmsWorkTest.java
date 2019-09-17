package hu.blackbelt.judo.tatami.asm2rdbms;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow.Builder.aNewParallelFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;

public class Asm2RdbmsWorkTest {

	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";
	public static final String MODEL_DIRECTORY = "model";
	public static final List<String> DIALECT_LIST = new LinkedList<String>(Arrays.asList("hsqldb", "oracle"));

	List<Asm2RdbmsWork> asm2RdbmsWorks = Lists.newArrayList();
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, AsmModel.AsmValidationException {
		AsmModel asmModel = loadAsmModel(
				asmLoadArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL)).name(NORTHWIND));

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(asmModel);
		
		DIALECT_LIST.forEach(dialect -> asm2RdbmsWorks.add(new Asm2RdbmsWork(transformationContext,
				new File(TARGET_TEST_CLASSES, "epsilon/transformations").toURI(), new File(MODEL_DIRECTORY).toURI(),
				dialect)));
	}

	@Test
	void testSimpleWorkflow() {
		//WorkFlow workflow = aNewSequentialFlow().execute(asm2RdbmsWork).build();
		
		WorkFlow workflow = aNewParallelFlow().execute(asm2RdbmsWorks.toArray(new Work[asm2RdbmsWorks.size()])).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
