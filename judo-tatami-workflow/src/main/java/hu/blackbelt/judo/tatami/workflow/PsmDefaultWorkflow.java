package hu.blackbelt.judo.tatami.workflow;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.core.ThrowingSupplier.sneakyThrows;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ConditionalFlow.Builder.aNewConditionalFlow;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow.Builder.aNewParallelFlow;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReportPredicate;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;
import lombok.Getter;

public class PsmDefaultWorkflow {

	@Getter
	private TransformationContext transformationContext;

	private DefaultWorkflowSetupParameters parameters;

	private WorkReport workReport;

	public void setUp(DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder builder)
			throws IOException, PsmModel.PsmValidationException {
		setUp(builder.build());
	}

	public void setUp(DefaultWorkflowSetupParameters params) throws IOException, PsmModel.PsmValidationException {

		// Loading Psm model here if it is not presented.
		PsmModel psmModel = ofNullable(params.getPsmModel()).orElseGet(() ->
				sneakyThrows(() -> loadPsmModel(psmLoadArgumentsBuilder()
					.inputStream(
						of(params.getPsmModelSourceURI()).orElseThrow(() ->
							new IllegalArgumentException("A psmModel and psmModelSourceUri have to be defined"))
							.toURL().openStream())
					.name(params.getModelName()))));

		transformationContext = new TransformationContext(params.getModelName());
		transformationContext.put(psmModel);
		this.parameters = params;
	}


	public WorkReport startDefaultWorkflow() {		

		Psm2AsmWork psm2AsmWork = new Psm2AsmWork(transformationContext,
				parameters.getPsm2AsmModelTransformationScriptURI());
		Psm2MeasureWork psm2MeasureWork = new Psm2MeasureWork(transformationContext,
				parameters.getPsm2MeasureModelTransformationScriptURI());
		
		List<Asm2RdbmsWork> asm2RdbmsWorks = new LinkedList<>();
		parameters.getDialectList().forEach(dialect -> asm2RdbmsWorks.add(new Asm2RdbmsWork(transformationContext,
				parameters.getAsm2RdbmsModelTransformationScriptURI(),
				parameters.getAsm2RdbmsModelTransformationModelURI(),
				dialect)));
		
		Asm2OpenAPIWork asm2OpenapiWork = new Asm2OpenAPIWork(transformationContext,
				parameters.getAsm2OpenapiModelTransformationScriptURI());
		
		Asm2SDKWork asm2sdkWork = new Asm2SDKWork(transformationContext, 
				parameters.getAsm2sdkModelTransformationScriptURI());

		Asm2JAXRSAPIWork asm2jaxrsapiWork = new Asm2JAXRSAPIWork(transformationContext,
				parameters.getAsm2jaxrsapiModelTransformationScriptURI());
		
		List<Rdbms2LiquibaseWork> rdbms2LiquibaseWorks = new LinkedList<>();
		parameters.getDialectList()
				.forEach(dialect -> rdbms2LiquibaseWorks.add(new Rdbms2LiquibaseWork(transformationContext,
						parameters.getRdbms2LiquibaseModelTransformationScriptURI(), dialect)));

		// ------------------ //
		// Workflow execution //
		// ------------------ //
		List<AbstractTransformationWork> asmWorks = Lists.newArrayList();
		asmWorks.addAll(asm2RdbmsWorks);
		asmWorks.add(asm2OpenapiWork);
		asmWorks.add(asm2jaxrsapiWork);
		asmWorks.add(asm2sdkWork);

		WorkFlow workflow = aNewConditionalFlow()
				.execute(
						aNewParallelFlow().execute(psm2AsmWork, psm2MeasureWork).build())
				.when(WorkReportPredicate.COMPLETED)
				.then(aNewConditionalFlow()
						.execute(aNewParallelFlow()
								.execute(asmWorks.toArray(new AbstractTransformationWork[asmWorks.size()])).build())
						.when(WorkReportPredicate.COMPLETED)
						.then(aNewParallelFlow().execute(rdbms2LiquibaseWorks
								.toArray(new AbstractTransformationWork[rdbms2LiquibaseWorks.size()])).build())
						.build())
				.build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		workReport = workFlowEngine.run(workflow);

		if (workReport.getStatus() == WorkStatus.FAILED) {
			throw new IllegalStateException("Transformation failed", workReport.getError());
		}
		
		List<String> rdbmsContexts = Lists.newArrayList();
		List<String> liquibaseContexts = Lists.newArrayList();
		List<String> asm2rdbmsTraceContexts = Lists.newArrayList();
		
		parameters.getDialectList().forEach(dialect -> rdbmsContexts.add("rdbms:" + dialect));
		parameters.getDialectList().forEach(dialect -> liquibaseContexts.add("liquibase:" + dialect));
		parameters.getDialectList().forEach(dialect -> asm2rdbmsTraceContexts.add("asm2rdbmstrace:" + dialect));

		boolean allExists = transformationContext.transformationContextVerifier
				.isClassExists(AsmModel.class)
				.isClassExists(MeasureModel.class)
				.isMultipleKeyExists(RdbmsModel.class,rdbmsContexts.toArray())
				.isMultipleKeyExists(LiquibaseModel.class,liquibaseContexts.toArray())
				.isClassExists(OpenapiModel.class)
				.isClassExists(Psm2AsmTransformationTrace.class)
				.isClassExists(Psm2MeasureTransformationTrace.class)
				.isClassExists(Asm2OpenAPITransformationTrace.class)
				.isMultipleKeyExists(Asm2RdbmsTransformationTrace.class, asm2rdbmsTraceContexts.toArray())
				.isKeyExists(InputStream.class, SDK_OUTPUT)
				.isKeyExists(InputStream.class, JAXRSAPI_OUTPUT)
				.isAllExists();

		if (!allExists) {
			throw new IllegalStateException("One or more models are missing for the transformation context.");
		}

		return workReport;
	}
}
