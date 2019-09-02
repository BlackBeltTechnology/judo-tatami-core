package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.DefaultWorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import java.io.File;
import java.net.URI;

public class DefaultWorkflow {
	
	TransformationContext transformationContext;
	public static final String MODEL_NAME = "northwind";
    public static final String DIALECT = "hsqldb";
    public static final String MODEL_DIRECTORY = "model";
	
	public void setUp()
	{
		transformationContext = new TransformationContext(MODEL_NAME);
		transformationContext.put(buildPsmModel().name(MODEL_NAME).build());
		transformationContext.put(Asm2RdbmsWork.RDBMS_DIALECT,DIALECT);
		transformationContext.put(Rdbms2LiquibaseWork.LIQUIBASE_DIALECT,DIALECT);
		transformationContext.put(Asm2RdbmsWork.RDBMS_EXCELMODEURI,new File(MODEL_DIRECTORY).toURI());
	}
	
	public WorkReport startDefaultWorkflow()
	{
		
		Psm2AsmWork psm2AsmWork = new Psm2AsmWork(transformationContext, new File("src/main/epsilon/transformations/asm").toURI());
		Psm2MeasureWork psm2MeasureWork = new Psm2MeasureWork(transformationContext, new File("src/main/epsilon/transformations/measure").toURI());
		Asm2RdbmsWork asm2RdbmsWork = new Asm2RdbmsWork(transformationContext, new File("src/main/epsilon/transformations/rdbms").toURI());
	    Asm2OpenAPIWork asm2OpenapiWork = new Asm2OpenAPIWork(transformationContext, new File("src/main/epsilon/transformations/openapi").toURI());
		Rdbms2LiquibaseWork rdbms2LiquibaseWork = new Rdbms2LiquibaseWork(transformationContext, new File("src/main/epsilon/transformations").toURI());
			
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
}