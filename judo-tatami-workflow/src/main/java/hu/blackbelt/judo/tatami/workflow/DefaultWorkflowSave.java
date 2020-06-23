package hu.blackbelt.judo.tatami.workflow;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.SaveArguments.expressionSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.SaveArguments.measureSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter.convertModelToFile;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.SaveArguments.scriptSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.core.ThrowingConsumer.executeWrapper;
import static hu.blackbelt.judo.tatami.script2operation.Script2OperationWork.OPERATION_OUTPUT;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;

public class DefaultWorkflowSave {

	public static void saveModels(TransformationContext transformationContext, File dest, List<String> dialectList) {
		saveModels(true, transformationContext, dest, dialectList);
	}

	public static void saveModels(boolean catchError, TransformationContext transformationContext, File dest, List<String> dialectList) {

		if (!dest.exists()) {
			throw new IllegalArgumentException("Destination doesn't exist!");
		}
		if (!dest.isDirectory()) {
			throw new IllegalArgumentException("Destination is not a directory!");
		}

		transformationContext.getByClass(AsmModel.class).ifPresent(executeWrapper(catchError, (m) ->
			m.saveAsmModel(asmSaveArgumentsBuilder().file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-asm.model"))))));

		transformationContext.getByClass(MeasureModel.class).ifPresent(executeWrapper(catchError, (m) ->
			m.saveMeasureModel(measureSaveArgumentsBuilder().file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-measure.model"))))));

		dialectList.forEach(dialect -> transformationContext.get(RdbmsModel.class, "rdbms:" + dialect)
				.ifPresent(executeWrapper(catchError, (m) -> m.saveRdbmsModel(
						rdbmsSaveArgumentsBuilder().file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "rdbms_" + dialect + ".model")))))));

		transformationContext.getByClass(ExpressionModel.class).ifPresent(executeWrapper(catchError, (m) ->
				m.saveExpressionModel(expressionSaveArgumentsBuilder().file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-expression.model"))))));

		transformationContext.getByClass(ScriptModel.class).ifPresent(executeWrapper(catchError, (m) ->
				m.saveScriptModel(scriptSaveArgumentsBuilder().file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-script.model"))))));

		transformationContext.getByClass(OpenapiModel.class).ifPresent(executeWrapper(catchError, (m) ->
			m.saveOpenapiModel(openapiSaveArgumentsBuilder().file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-openapi.model"))))));

		transformationContext.getByClass(OpenapiModel.class).ifPresent(executeWrapper(catchError, (m) -> m.getResourceSet().getResource(m.getUri(), false).getContents().forEach(api -> {
			convertModelToFile((API) api, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + ((API) api).getInfo().getTitle() + "-openapi.yaml")).getAbsolutePath(), OpenAPIExporter.Format.YAML);
			convertModelToFile((API) api, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + ((API) api).getInfo().getTitle() + "-openapi.json")).getAbsolutePath(), OpenAPIExporter.Format.JSON);
		})));

		dialectList.forEach(dialect -> transformationContext.get(LiquibaseModel.class, "liquibase:" + dialect)
				.ifPresent(executeWrapper(catchError, (m) -> m.saveLiquibaseModel(liquibaseSaveArgumentsBuilder()
						.file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "liquibase_" + dialect + ".changelog.xml")))))));

		transformationContext.getByClass(Psm2AsmTransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
			m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "psm2asm.model")))));

		transformationContext.getByClass(Psm2MeasureTransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
			m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "psm2measure.model")))));

		dialectList.forEach(dialect -> transformationContext
				.get(Asm2RdbmsTransformationTrace.class, "asm2rdbmstrace:" + dialect)
				.ifPresent(executeWrapper(catchError, (m) -> m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2rdbms_" + dialect + ".model"))))));


		transformationContext.getByClass(Asm2OpenAPITransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
			m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2openapi.model")))));

		transformationContext.get(InputStream.class, SDK_OUTPUT).ifPresent(executeWrapper(catchError, (m) -> {
			Files.copy(m, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2sdk.jar")).toPath());
			m.close();
		}));

		transformationContext.get(InputStream.class, OPERATION_OUTPUT).ifPresent(executeWrapper(catchError, (m) -> {
			Files.copy(m, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "script2operation.jar")).toPath());
			m.close();
		}));
		
		transformationContext.get(InputStream.class, JAXRSAPI_OUTPUT).ifPresent(executeWrapper(catchError, (m)-> {
			Files.copy(m, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2jaxrsapi.jar")).toPath());
			m.close();
		}));
	}

	private static File deleteFileIfExists(File file) {
		if (!file.isDirectory() && file.exists()) {
			file.delete();
		}
		return file;
	}
}
