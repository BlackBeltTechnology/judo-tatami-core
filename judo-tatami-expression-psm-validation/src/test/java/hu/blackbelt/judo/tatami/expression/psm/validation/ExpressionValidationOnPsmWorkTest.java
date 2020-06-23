package hu.blackbelt.judo.tatami.expression.psm.validation;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.buildExpressionModel;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.buildMeasureModel;

import static hu.blackbelt.judo.tatami.asm2expression.Asm2Expression.executeAsm2Expression;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.calculatePsm2MeasureTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@Disabled
class ExpressionValidationOnPsmWorkTest {

	public static final String NORTHWIND = "northwind";

    TransformationContext transformationContext;
    ExpressionValidationOnPsmWork expressionValidationOnPsmWork;

	@BeforeEach
	void setUp() throws Exception {
		Demo demo = new Demo();
		PsmModel psmModel = demo.fullDemo();

		AsmModel asmModel = buildAsmModel().name(NORTHWIND).build();
		executePsm2AsmTransformation(psmModel, asmModel);

		MeasureModel measureModel = buildMeasureModel().name(NORTHWIND).build();
		executePsm2MeasureTransformation(psmModel, measureModel);

		ExpressionModel expressionModel = buildExpressionModel().name(NORTHWIND).build();
		executeAsm2Expression(asmModel, measureModel, expressionModel);

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(expressionModel);
		transformationContext.put(psmModel);

		expressionValidationOnPsmWork = new ExpressionValidationOnPsmWork(transformationContext);
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(expressionValidationOnPsmWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		if (workReport.getError() != null) {
			log.error("Error found:", workReport.getError());
		}

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}

}