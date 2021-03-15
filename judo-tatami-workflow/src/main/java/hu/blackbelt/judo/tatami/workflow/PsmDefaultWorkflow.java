package hu.blackbelt.judo.tatami.workflow;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT_INTERNAL;
import static hu.blackbelt.judo.tatami.core.ThrowingSupplier.sneakyThrows;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ConditionalFlow.Builder.aNewConditionalFlow;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow.Builder.aNewParallelFlow;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.script2operation.Script2OperationWork.OPERATION_OUTPUT;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow;
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

	public PsmDefaultWorkflow(DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder builder) {
		this(builder.build());
	}

	public PsmDefaultWorkflow(DefaultWorkflowSetupParameters params) {

		// Loading Psm model here if it is not presented.
		PsmModel psmModel = ofNullable(params.getPsmModel()).orElseGet(() ->
				sneakyThrows(() -> loadPsmModel(psmLoadArgumentsBuilder()
					.inputStream(
						of(params.getPsmModelSourceURI()).orElseThrow(() ->
							new IllegalArgumentException("psmModel or psmModelSourceUri have to be defined"))
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
		WorkflowMetrics metrics = parameters.getEnableMetrics() ? defaultMetrics : null;

		if (parameters.getIgnorePsm2Asm() && parameters.getIgnorePsm2Measure()) {
			throw new IllegalArgumentException("All transformation path are ignored");
		}

		Optional<Work> validatePsmWork = !parameters.getValidateModels() ? Optional.empty() :
				Optional.of(new PsmValidationWork(transformationContext).withMetricsCollector(metrics));

		Optional<Work> createMeasure = parameters.getIgnorePsm2Measure() ? Optional.empty() :
				Optional.of(new Psm2MeasureWork(transformationContext).withMetricsCollector(metrics));
		Optional<Work> createAsmWork = parameters.getIgnorePsm2Asm() ? Optional.empty() :
				Optional.of(new Psm2AsmWork(transformationContext).withMetricsCollector(metrics));


		Optional<Work> createExpressionWork = (parameters.getIgnoreAsm2Expression() || !createMeasure.isPresent() || !createAsmWork.isPresent()) ? Optional.empty() :
				Optional.of(
						aNewSequentialFlow().named("").execute(
								Stream.of(
										Optional.of(new Asm2ExpressionWork(transformationContext).withMetricsCollector(metrics)),
										parameters.getValidateModels() ? Optional.of(new ExpressionValidationOnAsmWork(transformationContext).withMetricsCollector(metrics)) : Optional.empty()
								).filter(Optional::isPresent).map(Optional::get).toArray(Work[]::new)
						).build()
				);

		Optional<Work> createScriptWork = (parameters.getIgnoreAsm2Script() || !createAsmWork.isPresent() || !createMeasure.isPresent()) ? Optional.empty() :
				Optional.of(new Asm2ScriptWork(transformationContext).withMetricsCollector(metrics));

		Optional<Work> createOpenAPIWork = (parameters.getIgnoreAsm2Openapi() || !createAsmWork.isPresent()) ? Optional.empty() :
				Optional.of(new Asm2OpenAPIWork(transformationContext).withMetricsCollector(metrics));

		Optional<Work> createJAXRSAPIWork = (parameters.getIgnoreAsm2jaxrsapi() || !createAsmWork.isPresent()) ? Optional.empty() :
				Optional.of(new Asm2JAXRSAPIWork(transformationContext).withMetricsCollector(metrics));

		Optional<Work> createSDKWork = (parameters.getIgnoreAsm2sdk() || !createAsmWork.isPresent()) ? Optional.empty() :
				Optional.of(new Asm2SDKWork(transformationContext).withMetricsCollector(metrics));

		Optional<Work> createKeycloakWork = (parameters.getIgnoreAsm2Keycloak() || !createAsmWork.isPresent()) ? Optional.empty() :
				Optional.of(new Asm2KeycloakWork(transformationContext).withMetricsCollector(metrics));

		Optional<Work> createOperationWork = (parameters.getIgnoreScript2Operation() || !createScriptWork.isPresent()) ? Optional.empty() :
				Optional.of(new Script2OperationWork(transformationContext).withMetricsCollector(metrics));

		Stream<Optional<Work>> createRdbmsWorks = (parameters.getIgnoreAsm2Rdbms() || !createAsmWork.isPresent()) ? Stream.empty() :
				parameters.getDialectList()
						.stream().map(dialect -> Optional.of(aNewSequentialFlow().named("Sequential RDMS " + dialect + " work when COMPLETED Then Run Liquibase work")
									.execute(
											Stream.of(
													Optional.of(new Asm2RdbmsWork(transformationContext, dialect).withMetricsCollector(metrics)),
													parameters.getIgnoreRdbms2Liquibase() ? Optional.empty() :  Optional.of(new Rdbms2LiquibaseWork(transformationContext, dialect).withMetricsCollector(metrics))
											)
									).build())
				);

		WorkFlow workflow;

		if (parameters.getRunInParallel()) {
			workflow = aNewSequentialFlow()
					.named("ValidatePsm, PSM, ASM and Script transformations")
					.execute(
							validatePsmWork,
							Optional.of(
									aNewParallelFlow()
											.named("Parallel PSM Transformations")
											.execute(Stream.of(createAsmWork, createMeasure))
											.build()),
							Optional.of(
									aNewParallelFlow()
											.named("Parallel ASM Transformations")
											.execute(Stream.concat(
													Stream.of(createOpenAPIWork, createExpressionWork, createKeycloakWork, createJAXRSAPIWork, createSDKWork, createScriptWork),
													createRdbmsWorks
											))
											.build()),
							createOperationWork
					).build();
		} else {
			workflow = aNewSequentialFlow()
					.named("Run all transformations sequentially")
					.execute(
							Stream.concat(
									Stream.of(validatePsmWork,
											createAsmWork, createMeasure,
											createExpressionWork, createKeycloakWork, createJAXRSAPIWork, createSDKWork, createScriptWork, createOperationWork, createOpenAPIWork),
									createRdbmsWorks
							)
					).build();
		}

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		workReport = workFlowEngine.run(workflow);

		if (workReport.getStatus() == WorkStatus.FAILED) {
			throw new IllegalStateException("Transformation failed", workReport.getError());
		}

		TransformationContext.TransformationContextVerifier verifier = transformationContext.transformationContextVerifier;

		if (createMeasure.isPresent()) {
			verifier.isClassExists(MeasureModel.class);
			verifier.isClassExists(Psm2MeasureTransformationTrace.class);
		}
		if (createAsmWork.isPresent()) {
			verifier.isClassExists(AsmModel.class);
			verifier.isClassExists(Psm2AsmTransformationTrace.class);
		}


		if (createAsmWork.isPresent() && !parameters.getIgnoreAsm2Rdbms()) {
			verifier.isMultipleKeyExists(RdbmsModel.class, parameters.getDialectList().stream().map(d -> "rdbms:" + d).toArray());
			verifier.isMultipleKeyExists(Asm2RdbmsTransformationTrace.class, parameters.getDialectList().stream().map(d -> "asm2rdbmstrace:" + d).toArray());
			if (!parameters.getIgnoreRdbms2Liquibase()) {
				verifier.isMultipleKeyExists(LiquibaseModel.class, parameters.getDialectList().stream().map(d -> "liquibase:" + d).toArray());
			}
		}

		if (createExpressionWork.isPresent()) {
			verifier.isClassExists(ExpressionModel.class);
		}

		if (createScriptWork.isPresent()) {
			verifier.isClassExists(ScriptModel.class);
		}

		if (createOpenAPIWork.isPresent()) {
			verifier.isClassExists(OpenapiModel.class);
			verifier.isClassExists(Asm2OpenAPITransformationTrace.class);
		}

		if (createJAXRSAPIWork.isPresent()) {
			verifier.isKeyExists(InputStream.class, JAXRSAPI_OUTPUT);
		}

		if (createSDKWork.isPresent()) {
			verifier.isMultipleKeyExists(InputStream.class, SDK_OUTPUT, SDK_OUTPUT_INTERNAL);
		}

		if (createOperationWork.isPresent()) {
			verifier.isKeyExists(InputStream.class, OPERATION_OUTPUT);
		}

		if (createKeycloakWork.isPresent()) {
			verifier.isClassExists(KeycloakModel.class);
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
