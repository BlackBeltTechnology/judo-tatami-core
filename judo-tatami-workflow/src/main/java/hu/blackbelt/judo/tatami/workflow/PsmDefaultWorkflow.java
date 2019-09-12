package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;
import java.io.IOException;
import java.net.URI;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;

import lombok.Getter;

public class PsmDefaultWorkflow {

	@Getter
	private TransformationContext transformationContext;

	private URI psm2asmModelTransformationScriptRoot;
	private URI asm2openapiTransformationScriptRoot;
	private URI psm2measureTransformationScriptRoot;
	private URI asm2rdbmsTransformationScriptRoot;
	private URI rdbms2liquibaseTransformationScriptRoot;

	private WorkReport workReport;

	public void setUp(DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder builder)
			throws IOException, PsmModel.PsmValidationException {
		setUp(builder.build());
	}

	public void setUp(DefaultWorkflowSetupParameters params) throws IOException, PsmModel.PsmValidationException {
		transformationContext = new TransformationContext(params.getModelName());
		transformationContext.put(PsmModel.loadPsmModel(PsmModel.LoadArguments.psmLoadArgumentsBuilder()
				.file(params.getPsmModelSourceURI()).name(params.getModelName())));
		transformationContext.put(Asm2RdbmsWork.RDBMS_DIALECT, params.getDialect());
		transformationContext.put(Rdbms2LiquibaseWork.LIQUIBASE_DIALECT, params.getDialect());
		transformationContext.put(Asm2RdbmsWork.MODEL_URI, params.getModelURI());

		this.psm2asmModelTransformationScriptRoot = params.getPsm2asmModelTransformationScriptRoot();
		this.asm2openapiTransformationScriptRoot = params.getAsm2openapiModelTransformationScriptRoot();
		this.psm2measureTransformationScriptRoot = params.getPsm2measureModelTransformationScriptRoot();
		this.asm2rdbmsTransformationScriptRoot = params.getAsm2rdbmsModelTransformationScriptRoot();
		this.rdbms2liquibaseTransformationScriptRoot = params.getRdbms2liquibaseModelTransformationScriptRoot();
	}

	public WorkReport startDefaultWorkflow() {

		Psm2AsmWork psm2AsmWork = new Psm2AsmWork(transformationContext, psm2asmModelTransformationScriptRoot);
		Psm2MeasureWork psm2MeasureWork = new Psm2MeasureWork(transformationContext, psm2measureTransformationScriptRoot);
		Asm2RdbmsWork asm2RdbmsWork = new Asm2RdbmsWork(transformationContext, asm2rdbmsTransformationScriptRoot);
		Asm2OpenAPIWork asm2OpenapiWork = new Asm2OpenAPIWork(transformationContext, asm2openapiTransformationScriptRoot);
		Rdbms2LiquibaseWork rdbms2LiquibaseWork = new Rdbms2LiquibaseWork(transformationContext, rdbms2liquibaseTransformationScriptRoot);

		// ------------------ //
		// Workflow execution //
		// ------------------ //
		WorkFlow workflow = SequentialFlow.Builder.aNewSequentialFlow()
				.execute(ParallelFlow.Builder.aNewParallelFlow().execute(psm2AsmWork, psm2MeasureWork).build())
				.then(ParallelFlow.Builder.aNewParallelFlow().execute(asm2RdbmsWork, asm2OpenapiWork).build())
				.then(rdbms2LiquibaseWork).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		workReport = workFlowEngine.run(workflow);

		if (workReport.getStatus() == WorkStatus.FAILED) {
			throw new IllegalStateException("Transformation failed", workReport.getError());
		}

		boolean allExists = transformationContext.transformationContextVerifier
				.isClassExists(AsmModel.class)
				.isClassExists(MeasureModel.class)
				.isClassExists(RdbmsModel.class)
				.isClassExists(OpenapiModel.class)
				.isClassExists(LiquibaseModel.class)
				.isClassExists(Psm2AsmTransformationTrace.class)
				.isClassExists(Psm2MeasureTransformationTrace.class)
				.isClassExists(Asm2RdbmsTransformationTrace.class)
				.isClassExists(Asm2OpenAPITransformationTrace.class)
				.isAllExists();

		if (!allExists) {
			throw new IllegalStateException("One or more models are missing for the transformation context.");
		}

		return workReport;
	}
}
