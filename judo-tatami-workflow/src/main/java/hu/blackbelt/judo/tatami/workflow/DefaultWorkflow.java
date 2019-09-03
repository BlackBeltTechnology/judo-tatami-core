package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class DefaultWorkflow {
	
	TransformationContext transformationContext;
	public static final String MODEL_NAME = "northwind";
    public static final String DIALECT = "hsqldb";
    public static final String MODEL_DIRECTORY = "model";
    
    URI asmModelURI;
    URI openapiModelURI;
    URI measureModelURI;
    URI rdbmsModelURI;
    URI liquibaseModelURI;
    
    private WorkReport workReport;
	
	public void setUp(File psmModeldest,
					  URI asmModelURI,
					  URI openapiModelURI,
					  URI measureModelURI,
					  URI rdbmsModelURI,
					  URI liquibaseModelURI) throws IOException, PsmModel.PsmValidationException
	{
		transformationContext = new TransformationContext(MODEL_NAME);
		transformationContext.put(PsmModel.loadPsmModel(PsmModel.LoadArguments.psmLoadArgumentsBuilder()
				.file(psmModeldest)
				.name(MODEL_NAME)));
		transformationContext.put(Asm2RdbmsWork.RDBMS_DIALECT,DIALECT);
		transformationContext.put(Rdbms2LiquibaseWork.LIQUIBASE_DIALECT,DIALECT);
		transformationContext.put(Asm2RdbmsWork.RDBMS_EXCELMODEURI,new File(MODEL_DIRECTORY).toURI());
		
		this.asmModelURI = asmModelURI;
	    this.openapiModelURI = openapiModelURI;
	    this.measureModelURI = measureModelURI;
	    this.rdbmsModelURI = rdbmsModelURI;
	    this.liquibaseModelURI = liquibaseModelURI;
	}
	
	public WorkReport startDefaultWorkflow()
	{
		
		Psm2AsmWork psm2AsmWork = new Psm2AsmWork(transformationContext, asmModelURI);
		Psm2MeasureWork psm2MeasureWork = new Psm2MeasureWork(transformationContext, measureModelURI);
		Asm2RdbmsWork asm2RdbmsWork = new Asm2RdbmsWork(transformationContext, rdbmsModelURI);
	    Asm2OpenAPIWork asm2OpenapiWork = new Asm2OpenAPIWork(transformationContext, openapiModelURI);
		Rdbms2LiquibaseWork rdbms2LiquibaseWork = new Rdbms2LiquibaseWork(transformationContext, liquibaseModelURI);
			
		WorkFlow workflow = SequentialFlow.Builder.aNewSequentialFlow()
				.named("psm2asm&psm2measure, asm2dbms&asm2openapi, rdbms2liquibase")
		        .execute(ParallelFlow.Builder.aNewParallelFlow()
		        		.named("ps2asm, psm2measure")
		        		.execute(psm2AsmWork, psm2MeasureWork)
		        		.build())
		        .then(ParallelFlow.Builder.aNewParallelFlow()
		        		.named("asm2rdbms, asm2openapi")
		        		.execute(asm2RdbmsWork, asm2OpenapiWork)
		        		.build())
		        .then(rdbms2LiquibaseWork)
		        .build();
		
		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		workReport = workFlowEngine.run(workflow);
		
	     transformationContext.getByClass(AsmModel.class)
	     	.orElseThrow(() -> new IllegalStateException("Missing transformated AsmModel"));
	     transformationContext.getByClass(MeasureModel.class)
	     	.orElseThrow(() -> new IllegalStateException("Missing transformated MeasureModel"));
	     transformationContext.getByClass(RdbmsModel.class)
	     	.orElseThrow(() -> new IllegalStateException("Missing transformated RdbmsModel"));
	     transformationContext.getByClass(OpenapiModel.class)
	     	.orElseThrow(() -> new IllegalStateException("Missing transformated OpenAPIModel"));
	     transformationContext.getByClass(LiquibaseModel.class)
	     	.orElseThrow(() -> new IllegalStateException("Missing transformated LiquibaseModel"));
	     
	     return workReport;
	}
	
	public void saveModels(URI dest) 
			throws IOException,
				   AsmValidationException,
				   MeasureValidationException,
				   RdbmsValidationException,
				   OpenapiValidationException,
				   LiquibaseValidationException {
		
		if(workReport.getStatus() == WorkStatus.FAILED) {
			throw new IllegalStateException("Transformation not yet executed");
		}
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////// AsmModel saving ///////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////
		AsmModel asmModel = transformationContext.getByClass(AsmModel.class)
				.orElseThrow(() -> new IllegalStateException("Missing transformated AsmModel"));
		File asmModelDest = new File(dest.toString().replace("file:", ""),"asm.model");
		asmModelDest.createNewFile();
		asmModel.saveAsmModel(AsmModel.SaveArguments
				.asmSaveArgumentsBuilder()
				.file(asmModelDest)
				.outputStream(new FileOutputStream(asmModelDest)));
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////// MeasureModel saving ////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////
		MeasureModel measureModel = transformationContext.getByClass(MeasureModel.class)
				.orElseThrow(() -> new IllegalStateException("Missing transformated MeasureModel"));
		File measureModelDest = new File(dest.toString().replace("file:", ""),"measure.model");
		measureModelDest.createNewFile();
		measureModel.saveMeasureModel(MeasureModel.SaveArguments
				.measureSaveArgumentsBuilder()
				.file(measureModelDest)
				.outputStream(new FileOutputStream(measureModelDest)));
		
		
				
		//////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////// RdbmsModel saving ////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////
		RdbmsModel rdbmsModel = transformationContext.getByClass(RdbmsModel.class)
				.orElseThrow(() -> new IllegalStateException("Missing transformated RdbmsModel"));
		File rdbmsModelDest = new File(dest.toString().replace("file:", ""),"rdbms.model");
		rdbmsModelDest.createNewFile();
		rdbmsModel.saveRdbmsModel(RdbmsModel.SaveArguments
				.rdbmsSaveArgumentsBuilder()
				.file(rdbmsModelDest)
				.outputStream(new FileOutputStream(rdbmsModelDest)));
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////// OpenapiModel saving //////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////
		OpenapiModel openapiModel = transformationContext.getByClass(OpenapiModel.class)
				.orElseThrow(() -> new IllegalStateException("Missing transformated OpenapiModel"));
		File openapiModelDest = new File(dest.toString().replace("file:", ""),"openapi.model");
		openapiModelDest.createNewFile();
		openapiModel.saveOpenapiModel(OpenapiModel.SaveArguments
				.openapiSaveArgumentsBuilder()
				.file(openapiModelDest)
				.outputStream(new FileOutputStream(openapiModelDest)));

		
		
		//////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////// LiquibaseModel saving //////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////
		LiquibaseModel liquibaseModel = transformationContext.getByClass(LiquibaseModel.class)
				.orElseThrow(() -> new IllegalStateException("Missing transformated LiquibaseModel"));	
		File liquibaseModelDest = new File(dest.toString().replace("file:", ""),"liquibase.changelog.xml");
		liquibaseModelDest.createNewFile();
		liquibaseModel.saveLiquibaseModel(LiquibaseModel.SaveArguments
				.liquibaseSaveArgumentsBuilder()
				.file(liquibaseModelDest)
				.outputStream(new FileOutputStream(liquibaseModelDest)));
		
	}
}