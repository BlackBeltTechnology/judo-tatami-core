package hu.blackbelt.judo.tatami.workflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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


		AsmModel asmModel = transformationContext.getByClass(AsmModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated AsmModel"));
		File asmModelDest = new File(dest, "asm.model");
		asmModel.saveAsmModel(AsmModel.SaveArguments.asmSaveArgumentsBuilder().file(asmModelDest)
				.outputStream(new FileOutputStream(asmModelDest)));


		MeasureModel measureModel = transformationContext.getByClass(MeasureModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated MeasureModel"));
		File measureModelDest = new File(dest, "measure.model");
		measureModel.saveMeasureModel(MeasureModel.SaveArguments.measureSaveArgumentsBuilder().file(measureModelDest)
				.outputStream(new FileOutputStream(measureModelDest)));


		RdbmsModel rdbmsModel = transformationContext.getByClass(RdbmsModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated RdbmsModel"));
		File rdbmsModelDest = new File(dest, "rdbms.model");
		rdbmsModel.saveRdbmsModel(RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(rdbmsModelDest)
				.outputStream(new FileOutputStream(rdbmsModelDest)));


		OpenapiModel openapiModel = transformationContext.getByClass(OpenapiModel.class)
				.orElseThrow(() -> new IllegalStateException("Cannot save model: Missing transformated OpenapiModel"));
		File openapiModelDest = new File(dest, "openapi.model");
		openapiModel.saveOpenapiModel(OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder().file(openapiModelDest)
				.outputStream(new FileOutputStream(openapiModelDest)));


		LiquibaseModel liquibaseModel = transformationContext.getByClass(LiquibaseModel.class).orElseThrow(
				() -> new IllegalStateException("Cannot save model: Missing transformated LiquibaseModel"));
		File liquibaseModelDest = new File(dest, "liquibase.changelog.xml");
		liquibaseModel.saveLiquibaseModel(LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder()
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
	}

}
