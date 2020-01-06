package hu.blackbelt.judo.tatami.workflow;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.SaveArguments.expressionSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.SaveArguments.measureSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.SaveArguments.scriptSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.core.ThrowingConsumer.throwingConsumerWrapper;
import static hu.blackbelt.judo.tatami.script2operation.Script2OperationWork.OPERATION_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;
import hu.blackbelt.judo.tatami.script2operation.Script2OperationWork;

public class DefaultWorkflowSave {
	
	public static void saveModels(TransformationContext transformationContext, File dest, List<String> dialectList) throws IOException, AsmValidationException,
		MeasureValidationException, RdbmsValidationException, OpenapiValidationException, LiquibaseValidationException {

		if (!dest.exists()) {
			throw new IllegalArgumentException("Destination doesn't exist!");
		}
		if (!dest.isDirectory()) {
			throw new IllegalArgumentException("Destination is not a directory!");
		}

		transformationContext.getByClass(AsmModel.class).ifPresent(throwingConsumerWrapper((m) ->  
			m.saveAsmModel(asmSaveArgumentsBuilder().file(new File(dest, transformationContext.getModelName() + "-asm.model")))));

		transformationContext.getByClass(MeasureModel.class).ifPresent(throwingConsumerWrapper((m) ->  
			m.saveMeasureModel(measureSaveArgumentsBuilder().file(new File(dest, transformationContext.getModelName() + "-measure.model")))));

		dialectList.forEach(dialect -> transformationContext.get(RdbmsModel.class, "rdbms:" + dialect)
				.ifPresent(throwingConsumerWrapper((m) -> m.saveRdbmsModel(
						rdbmsSaveArgumentsBuilder().file(new File(dest, transformationContext.getModelName() + "-" + "rdbms_" + dialect + ".model"))))));

		transformationContext.getByClass(ExpressionModel.class).ifPresent(throwingConsumerWrapper((m) ->
				m.saveExpressionModel(expressionSaveArgumentsBuilder().file(new File(dest, transformationContext.getModelName() + "-expression.model")))));

		transformationContext.getByClass(ScriptModel.class).ifPresent(throwingConsumerWrapper((m) ->
				m.saveScriptModel(scriptSaveArgumentsBuilder().file(new File(dest, transformationContext.getModelName() + "-script.model")))));

		transformationContext.getByClass(OpenapiModel.class).ifPresent(throwingConsumerWrapper((m) ->
			m.saveOpenapiModel(openapiSaveArgumentsBuilder().file(new File(dest, transformationContext.getModelName() + "-openapi.model")))));
		
		dialectList.forEach(dialect -> transformationContext.get(LiquibaseModel.class, "liquibase:" + dialect)
				.ifPresent(throwingConsumerWrapper((m) -> m.saveLiquibaseModel(liquibaseSaveArgumentsBuilder()
						.file(new File(dest, transformationContext.getModelName() + "-" + "liquibase_" + dialect + ".changelog.xml"))))));

		transformationContext.getByClass(Psm2AsmTransformationTrace.class).ifPresent(throwingConsumerWrapper((m) ->
			m.save(new File(dest, transformationContext.getModelName() + "-" + "psm2asm.model"))));

		transformationContext.getByClass(Psm2MeasureTransformationTrace.class).ifPresent(throwingConsumerWrapper((m) ->
			m.save(new File(dest, transformationContext.getModelName() + "-" + "psm2measure.model"))));

		dialectList.forEach(dialect -> transformationContext
				.get(Asm2RdbmsTransformationTrace.class, "asm2rdbmstrace:" + dialect)
				.ifPresent(throwingConsumerWrapper((m) -> m.save(new File(dest, transformationContext.getModelName() + "-" + "asm2rdbms_" + dialect + ".model")))));


		transformationContext.getByClass(Asm2OpenAPITransformationTrace.class).ifPresent(throwingConsumerWrapper((m) ->
			m.save(new File(dest, transformationContext.getModelName() + "-" + "asm2openapi.model"))));

		transformationContext.get(InputStream.class, SDK_OUTPUT).ifPresent(throwingConsumerWrapper((m) ->
				Files.copy(m, new File(dest, transformationContext.getModelName() + "-" + "asm2sdk.jar").toPath())));

		transformationContext.get(InputStream.class, OPERATION_OUTPUT).ifPresent(throwingConsumerWrapper((m) ->
			Files.copy(m, new File(dest, transformationContext.getModelName() + "-" + "script2operation.jar").toPath())));
		
		transformationContext.get(InputStream.class, JAXRSAPI_OUTPUT).ifPresent(throwingConsumerWrapper((m)->
			Files.copy(m, new File(dest, transformationContext.getModelName() + "-" + "asm2jaxrsapi.jar").toPath())));
			
	}

}
