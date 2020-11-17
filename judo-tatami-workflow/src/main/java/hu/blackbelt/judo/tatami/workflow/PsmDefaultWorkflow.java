package hu.blackbelt.judo.tatami.workflow;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT_INTERNAL;
import static hu.blackbelt.judo.tatami.core.ThrowingSupplier.sneakyThrows;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ConditionalFlow.Builder.aNewConditionalFlow;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.script2operation.Script2OperationWork.OPERATION_OUTPUT;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork;
import hu.blackbelt.judo.tatami.asm2keycloak.Asm2KeycloakWork;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.asm2script.Asm2ScriptWork;
import hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.*;
import hu.blackbelt.judo.tatami.expression.asm.validation.ExpressionValidationOnAsmWork;
import hu.blackbelt.judo.tatami.psm.validation.PsmValidationWork;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;
import hu.blackbelt.judo.tatami.script2operation.Script2OperationWork;
import hu.blackbelt.judo.tatami.asm2expression.Asm2ExpressionWork;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PsmDefaultWorkflow {

	@Getter
	private TransformationContext transformationContext;

	private DefaultWorkflowSetupParameters parameters;

	private WorkReport workReport;

	private WorkflowMetrics defaultMetrics = new DefaultWorkflowMetricsCollector();

	public PsmDefaultWorkflow(DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder builder)
			throws IOException, PsmModel.PsmValidationException {
		this(builder.build());
	}

	public PsmDefaultWorkflow(DefaultWorkflowSetupParameters params) throws IOException, PsmModel.PsmValidationException {

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

		// ------------------ //
		// Workflow execution //
		// ------------------ //
		List<AbstractTransformationWork> psmWorks = Lists.newArrayList();
		List<AbstractTransformationWork> asmWorks = Lists.newArrayList();
		List<AbstractTransformationWork> rdbmsWorks = new ArrayList<>();
		List<AbstractTransformationWork> asm2RdbmsWorks = new ArrayList<>();
		List<AbstractTransformationWork> scriptWorks = new ArrayList<>();

		WorkflowMetrics metrics = parameters.getEnableMetrics() ? defaultMetrics : null;

		if (parameters.getIgnorePsm2Asm() && parameters.getIgnorePsm2Measure()) {
			throw new IllegalArgumentException("All transformation path are ignored");
		}

		if (parameters.getValidateModels()) {
			psmWorks.add(new PsmValidationWork(transformationContext).withMetricsCollector(metrics));
		}

		if (!parameters.getIgnorePsm2Measure()) {
			psmWorks.add(new Psm2MeasureWork(transformationContext).withMetricsCollector(metrics));
		}

		if (!parameters.getIgnorePsm2Asm()) {
			psmWorks.add(new Psm2AsmWork(transformationContext).withMetricsCollector(metrics));

			if (!parameters.getIgnoreAsm2Rdbms()) {
				parameters.getDialectList().forEach(dialect -> {
					asm2RdbmsWorks.add(new Asm2RdbmsWork(transformationContext, dialect).withMetricsCollector(metrics));
				});
				asmWorks.addAll(asm2RdbmsWorks);
				if (!parameters.getIgnoreRdbms2Liquibase()) {
					parameters.getDialectList()
							.forEach(dialect -> rdbmsWorks.add(new Rdbms2LiquibaseWork(transformationContext, dialect).withMetricsCollector(metrics)));
				}
			}
			if (!parameters.getIgnoreAsm2Expression() && !parameters.getIgnorePsm2Measure()) {
				asmWorks.add(new Asm2ExpressionWork(transformationContext).withMetricsCollector(metrics));
				if (parameters.getValidateModels()) {
					asmWorks.add(new ExpressionValidationOnAsmWork(transformationContext).withMetricsCollector(metrics));
				}
			}
			if (!parameters.getIgnoreAsm2Script() && !parameters.getIgnorePsm2Measure()) {
				asmWorks.add(new Asm2ScriptWork(transformationContext).withMetricsCollector(metrics));
			}

			if (!parameters.getIgnoreAsm2Openapi()) {
				asmWorks.add(new Asm2OpenAPIWork(transformationContext).withMetricsCollector(metrics));
			}
			if (!parameters.getIgnoreAsm2jaxrsapi()) {
				asmWorks.add(new Asm2JAXRSAPIWork(transformationContext).withMetricsCollector(metrics));
			}
			if (!parameters.getIgnoreAsm2sdk()) {
				asmWorks.add(new Asm2SDKWork(transformationContext).withMetricsCollector(metrics));
			}
			if (!parameters.getIgnoreScript2Operation() && !parameters.getIgnoreAsm2Script() && !parameters.getIgnorePsm2Measure()) {
				scriptWorks.add(new Script2OperationWork(transformationContext).withMetricsCollector(metrics));
			}
			if (!parameters.getIgnoreAsm2Keycloak()) {
				asmWorks.add(new Asm2KeycloakWork(transformationContext).withMetricsCollector(metrics));
			}
		}

		WorkFlow workflow = aNewConditionalFlow()
				.named("Conditional when all PSM Transformations COMPLETED Run All ASM Transformations")
				.execute(aNewSequentialFlow()
						.named("PSM Transformations")
						.execute(psmWorks.toArray(new AbstractTransformationWork[psmWorks.size()]))
						.build())
				.when(WorkReportPredicate.COMPLETED)
				.then(aNewConditionalFlow()
						.named("Conditional when all ASM Transformations COMPLETED Run All RDBMS Transformations")
						.execute(aNewSequentialFlow()
								.named("Sequential ASM Transformations")
								.execute(asmWorks.toArray(new AbstractTransformationWork[asmWorks.size()]))
								.build())
						.when(WorkReportPredicate.COMPLETED)
						.then(aNewConditionalFlow()
								.named("Conditional when all RDBMS Transformations COMPLETED Run All Script Transformation")
								.execute(aNewSequentialFlow()
												.named("Sequential RDBMS Transformations")
												.execute(rdbmsWorks.toArray(new AbstractTransformationWork[rdbmsWorks.size()]))
												.build())
								.when(WorkReportPredicate.COMPLETED)
								.then(aNewSequentialFlow()
										.named("Sequential Script Transformations")
										.execute(
												scriptWorks.toArray(new AbstractTransformationWork[scriptWorks.size()]))
										.build())
								.build())
						.build())
				.build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		workReport = workFlowEngine.run(workflow);

		if (workReport.getStatus() == WorkStatus.FAILED) {
			throw new IllegalStateException("Transformation failed", workReport.getError());
		}

		TransformationContext.TransformationContextVerifier verifier = transformationContext.transformationContextVerifier;

		if (!parameters.getIgnorePsm2Measure()) {
			verifier.isClassExists(MeasureModel.class);
			verifier.isClassExists(Psm2MeasureTransformationTrace.class);
		}
		if (!parameters.getIgnorePsm2Asm()) {
			verifier.isClassExists(AsmModel.class);
			verifier.isClassExists(Psm2AsmTransformationTrace.class);

			if (!parameters.getIgnoreAsm2Rdbms()) {
				verifier.isMultipleKeyExists(RdbmsModel.class, parameters.getDialectList().stream().map(d -> "rdbms:" + d).toArray());
				verifier.isMultipleKeyExists(Asm2RdbmsTransformationTrace.class, parameters.getDialectList().stream().map(d -> "asm2rdbmstrace:" + d).toArray());
				if (!parameters.getIgnoreRdbms2Liquibase()) {
					verifier.isMultipleKeyExists(LiquibaseModel.class, parameters.getDialectList().stream().map(d -> "liquibase:" + d).toArray());
				}
			}
			if (!parameters.getIgnoreAsm2Expression() && !parameters.getIgnorePsm2Measure()) {
				verifier.isClassExists(ExpressionModel.class);
			}
			if (!parameters.getIgnoreAsm2Script() && !parameters.getIgnorePsm2Measure()) {
				verifier.isClassExists(ScriptModel.class);
			}
			if (!parameters.getIgnoreAsm2Openapi()) {
				verifier.isClassExists(OpenapiModel.class);
				verifier.isClassExists(Asm2OpenAPITransformationTrace.class);
			}
			if (!parameters.getIgnoreAsm2jaxrsapi()) {
				verifier.isKeyExists(InputStream.class, JAXRSAPI_OUTPUT);
			}
			if (!parameters.getIgnoreAsm2sdk()) {
				verifier.isMultipleKeyExists(InputStream.class, SDK_OUTPUT, SDK_OUTPUT_INTERNAL);
			}
			if (!parameters.getIgnoreScript2Operation() && !parameters.getIgnoreAsm2Script() && !parameters.getIgnorePsm2Measure()) {
				verifier.isKeyExists(InputStream.class, OPERATION_OUTPUT);
			}
			if (!parameters.getIgnoreAsm2Keycloak()) {
				verifier.isClassExists(KeycloakModel.class);
			}
		}
		if (!verifier.isAllExists()) {
			throw new IllegalStateException("One or more models are missing for the transformation context.");
		}

		if (parameters.getEnableMetrics()) {
			log.info("PSM default workflow summary: {}", metrics.getExecutionTimes().entrySet().stream()
					.map(e -> "\n  - " + e.getKey() + ": " + e.getValue()).collect(Collectors.joining()));
		}

		return workReport;
	}
}
