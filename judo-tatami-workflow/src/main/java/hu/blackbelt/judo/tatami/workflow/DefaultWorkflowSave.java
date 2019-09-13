package hu.blackbelt.judo.tatami.workflow;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.SaveArguments.measureSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork.JAXRSAPI_OUTPUT;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDKWork.SDK_OUTPUT;
import static hu.blackbelt.judo.tatami.core.ThrowingConsumer.throwingConsumerWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPIWork;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;

public class DefaultWorkflowSave {
	
	public static void saveModels(TransformationContext transformationContext, File dest) throws IOException, AsmValidationException, MeasureValidationException,
			RdbmsValidationException, OpenapiValidationException, LiquibaseValidationException {

		if (!dest.exists()) {
			throw new IllegalArgumentException("Destination doesn't exist!");
		}
		if (!dest.isDirectory()) {
			throw new IllegalArgumentException("Destination is not a directory!");
		}

		transformationContext.getByClass(AsmModel.class).ifPresent(throwingConsumerWrapper((m) ->  
			m.saveAsmModel(asmSaveArgumentsBuilder().file(new File(dest, "asm.model")))));

		MeasureModel measureModel = transformationContext.getByClass(MeasureModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated MeasureModel"));
		File measureModelDest = new File(dest, "measure.model");
		measureModel.saveMeasureModel(measureSaveArgumentsBuilder().file(measureModelDest)
				.outputStream(new FileOutputStream(measureModelDest)));


		RdbmsModel rdbmsModel = transformationContext.getByClass(RdbmsModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated RdbmsModel"));
		File rdbmsModelDest = new File(dest, "rdbms.model");
		rdbmsModel.saveRdbmsModel(rdbmsSaveArgumentsBuilder().file(rdbmsModelDest)
				.outputStream(new FileOutputStream(rdbmsModelDest)));


		OpenapiModel openapiModel = transformationContext.getByClass(OpenapiModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated OpenapiModel"));
		File openapiModelDest = new File(dest, "openapi.model");
		openapiModel.saveOpenapiModel(openapiSaveArgumentsBuilder().file(openapiModelDest)
				.outputStream(new FileOutputStream(openapiModelDest)));


		LiquibaseModel liquibaseModel = transformationContext.getByClass(LiquibaseModel.class).orElseThrow(
				() -> new IllegalStateException("Cannot save model: Missing transformated LiquibaseModel"));
		File liquibaseModelDest = new File(dest, "liquibase.changelog.xml");
		liquibaseModel.saveLiquibaseModel(liquibaseSaveArgumentsBuilder()
				.file(liquibaseModelDest).outputStream(new FileOutputStream(liquibaseModelDest)));


		transformationContext.getByClass(Psm2AsmTransformationTrace.class).orElseThrow(
				() -> new IllegalStateException("Cannot save transformation trace: Missing Psm2AsmTransformationTrace"))
				.save(new File(dest, "psm2asm.model"));

		transformationContext.getByClass(Psm2MeasureTransformationTrace.class)
				.orElseThrow(() -> new IllegalStateException(
						"Cannot save transformation trace: Missing Psm2MeasureTransformationTrace"))
				.save(new File(dest, "psm2measure.model"));


		transformationContext.getByClass(Asm2RdbmsTransformationTrace.class)
				.orElseThrow(() -> new IllegalStateException(
						"Cannot save transformation trace: Missing Asm2RdbmsTransformationTrace"))
				.save(new File(dest, "asm2rdbms.model"));

		
		transformationContext.getByClass(Asm2OpenAPITransformationTrace.class)
				.orElseThrow(() -> new IllegalStateException(
						"Cannot save transformation trace: Missing Asm2OpenAPITransformationTrace"))
				.save(new File(dest, "asm2openapi.model"));
		
		try (InputStream asm2sdkBundle = transformationContext.get(InputStream.class, SDK_OUTPUT)
				.orElseThrow(() -> new IllegalStateException(
						"Cannot save bundle: Missing Asm2SdkBundle"))) {
			Files.copy(asm2sdkBundle, new File(dest, "asm2sdk.jar").toPath());    			
		};
		
		try (InputStream asm2jaxrsapiBundle = transformationContext.get(InputStream.class, JAXRSAPI_OUTPUT)
				.orElseThrow(() -> new IllegalStateException(
						"Cannot save bundle: Missing Asm2JaxrsapiBundle"))) {
			Files.copy(asm2jaxrsapiBundle, new File(dest, "asm2jaxrsapi.jar").toPath());    			
		};
	}

}
