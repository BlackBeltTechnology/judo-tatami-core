package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.loadPsmModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class DefaultWorkflow {
	
	TransformationContext transformationContext;
	public static final String MODEL_NAME = "";
    public static final String DIALECT = "hsqldb";
    public static final String MODEL_DIRECTORY = "model";
    
    URI asmModelURI;
    URI openapiModelURI;
    URI measureModelURI;
    URI rdbmsModelURI;
    URI liquibaseModelURI;
	
	public void setUp(File psmModeldest, URI asmModelURI, URI openapiModelURI, URI measureModelURI, URI rdbmsModelURI, URI liquibaseModelURI) throws IOException, PsmModel.PsmValidationException
	{
		transformationContext = new TransformationContext(MODEL_NAME);
		transformationContext.put(PsmModel.buildPsmModel().name(MODEL_NAME).build());
		/*transformationContext.put(loadPsmModel(psmLoadArgumentsBuilder()
				.file(psmModeldest)
				.name(MODEL_NAME)));*/
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
				.named("psm2asm&psm2measure, asm2dbms&measure, rdbms2liquibase")
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
		WorkReport workReport = workFlowEngine.run(workflow);
		
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
	
	///This Util will save out the generated model files to a defined external directory.
	public void saveModels(URI dest)
	{
		
	}
	
	
}