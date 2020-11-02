package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.keycloak.Realm;
import hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel;
import hu.blackbelt.judo.meta.keycloak.runtime.exporter.KeycloakConfigurationExporter;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.asm2keycloak.Asm2KeycloakTransformationTrace;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.SaveArguments.expressionSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel.SaveArguments.keycloakSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.SaveArguments.measureSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter.convertModelToFile;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.script.runtime.ScriptModel.SaveArguments.scriptSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT_INTERNAL;
import static hu.blackbelt.judo.tatami.core.ThrowingConsumer.executeWrapper;
import static hu.blackbelt.judo.tatami.script2operation.Script2OperationWork.OPERATION_OUTPUT;

public class DefaultWorkflowSave {

	private static final boolean VALIDATE_MODELS_ON_SAVE = false; // do not validate models on save

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

		transformationContext.getByClass(PsmModel.class).ifPresent(executeWrapper(catchError, (m) ->
				m.savePsmModel(psmSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-psm.model"))))));

		transformationContext.getByClass(AsmModel.class).ifPresent(executeWrapper(catchError, (m) ->
			m.saveAsmModel(asmSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-asm.model"))))));

		transformationContext.getByClass(MeasureModel.class).ifPresent(executeWrapper(catchError, (m) ->
			m.saveMeasureModel(measureSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-measure.model"))))));

		dialectList.forEach(dialect -> transformationContext.get(RdbmsModel.class, "rdbms:" + dialect)
				.ifPresent(executeWrapper(catchError, (m) -> m.saveRdbmsModel(rdbmsSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE)
						.file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "rdbms_" + dialect + ".model")))))));

		transformationContext.getByClass(ExpressionModel.class).ifPresent(executeWrapper(catchError, (m) ->
				m.saveExpressionModel(expressionSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-expression.model"))))));

		transformationContext.getByClass(ScriptModel.class).ifPresent(executeWrapper(catchError, (m) ->
				m.saveScriptModel(scriptSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-script.model"))))));

		transformationContext.getByClass(OpenapiModel.class).ifPresent(executeWrapper(catchError, (m) ->
			m.saveOpenapiModel(openapiSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-openapi.model"))))));

		transformationContext.getByClass(KeycloakModel.class).ifPresent(executeWrapper(catchError, (m) ->
				m.saveKeycloakModel(keycloakSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE).file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-keycloak.model"))))));

		transformationContext.getByClass(OpenapiModel.class).ifPresent(executeWrapper(catchError, (m) -> m.getResourceSet().getResource(m.getUri(), false).getContents().forEach(api -> {
			convertModelToFile((API) api, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + ((API) api).getInfo().getTitle() + "-openapi.yaml")).getAbsolutePath(), OpenAPIExporter.Format.YAML);
			convertModelToFile((API) api, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + ((API) api).getInfo().getTitle() + "-openapi.json")).getAbsolutePath(), OpenAPIExporter.Format.JSON);
		})));

		transformationContext.getByClass(KeycloakModel.class).ifPresent(executeWrapper(catchError, (m) -> m.getResourceSet().getResource(m.getUri(), false).getContents().stream().forEach(realm ->
				new KeycloakConfigurationExporter((Realm) realm).writeConfigurationToFile(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + ((Realm) realm).getRealm() + "-keycloak.json"))))));

		dialectList.forEach(dialect -> transformationContext.get(LiquibaseModel.class, "liquibase:" + dialect)
				.ifPresent(executeWrapper(catchError, (m) -> m.saveLiquibaseModel(liquibaseSaveArgumentsBuilder().validateModel(VALIDATE_MODELS_ON_SAVE)
						.file(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "liquibase_" + dialect + ".changelog.xml")))))));

		transformationContext.getByClass(Esm2PsmTransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
				m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "esm2psm.model")))));

		transformationContext.getByClass(Psm2AsmTransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
			m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "psm2asm.model")))));

		transformationContext.getByClass(Psm2MeasureTransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
			m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "psm2measure.model")))));

		dialectList.forEach(dialect -> transformationContext
				.get(Asm2RdbmsTransformationTrace.class, "asm2rdbmstrace:" + dialect)
				.ifPresent(executeWrapper(catchError, (m) -> m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2rdbms_" + dialect + ".model"))))));


		transformationContext.getByClass(Asm2OpenAPITransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
			m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2openapi.model")))));

		transformationContext.getByClass(Asm2KeycloakTransformationTrace.class).ifPresent(executeWrapper(catchError, (m) ->
				m.save(deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2keycloak.model")))));

		transformationContext.get(InputStream.class, SDK_OUTPUT).ifPresent(executeWrapper(catchError, (m) -> {
			Files.copy(m, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2sdk.jar")).toPath());
			m.close();
		}));
		
		transformationContext.get(InputStream.class, SDK_OUTPUT_INTERNAL).ifPresent(executeWrapper(catchError, (m) -> {
			Files.copy(m, deleteFileIfExists(new File(dest, transformationContext.getModelName() + "-" + "asm2sdk-internal.jar")).toPath());
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
