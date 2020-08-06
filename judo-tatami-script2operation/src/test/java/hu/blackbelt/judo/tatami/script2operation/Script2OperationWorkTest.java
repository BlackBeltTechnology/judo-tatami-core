package hu.blackbelt.judo.tatami.script2operation;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static hu.blackbelt.judo.tatami.asm2script.Asm2Script.executeAsm2Script;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class Script2OperationWorkTest {
	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";

	Script2OperationWork script2OperationWork;
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws Exception {
        final PsmModel psmModel = new Demo().fullDemo();

        // Create empty ASM model
        AsmModel asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        // Create empty Measure model
        MeasureModel measureModel = MeasureModel.buildMeasureModel()
                .name(NORTHWIND)
                .build();
        
        // Create empty Script model
        ScriptModel scriptModel = ScriptModel.buildScriptModel()
                .name(NORTHWIND)
                .build();
        
        executePsm2AsmTransformation(psmModel, asmModel);
        executePsm2MeasureTransformation(psmModel, measureModel);
        executeAsm2Script(asmModel, measureModel, scriptModel);
        
		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(scriptModel);

		script2OperationWork = new Script2OperationWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(script2OperationWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		log.info("Workflow completed with status {}", workReport.getStatus(), workReport.getError());
		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
