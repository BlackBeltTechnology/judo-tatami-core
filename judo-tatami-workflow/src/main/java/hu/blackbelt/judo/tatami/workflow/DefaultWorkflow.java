package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPIWork;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsWork;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmWork;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureWork;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseWork;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.buildMeasureModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.LoadArguments.psmLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;

import java.io.File;
import java.net.URI;

import org.apache.commons.collections.functors.NullIsExceptionPredicate;

public class DefaultWorkflow {
	
	TransformationContext transformationContext;
	
	public static final String MODEL_NAME = "northwind";
    
    public static final String DIALECT = "";
	
	URI transformationScriptRoot;
	
	public void setUp(URI transformationScriptRoot) throws Exception
	{
		//EZT MÉG CSISZOLNI KELL
		transformationContext = new TransformationContext(MODEL_NAME);
		//PSM model
		transformationContext.put(buildPsmModel().build());
		//tfScript stuff
		this.transformationScriptRoot = transformationScriptRoot;
	}
	
	public void startDefaultWorkflow() throws NullPointerException
	{
		Psm2AsmWork psm2AsmWork = new Psm2AsmWork(transformationContext, transformationScriptRoot);
		Psm2MeasureWork psm2MeasureWork = new Psm2MeasureWork(transformationContext, transformationScriptRoot);
		Asm2RdbmsWork asm2RdbmsWork = new Asm2RdbmsWork(transformationContext, transformationScriptRoot);
	    Asm2OpenAPIWork asm2OpenapiWork = new Asm2OpenAPIWork(transformationContext, transformationScriptRoot);
		Rdbms2LiquibaseWork rdbms2LiquibaseWork = new Rdbms2LiquibaseWork(transformationContext, transformationScriptRoot);
		
		WorkFlow workflow = SequentialFlow.Builder.aNewSequentialFlow()
		        .execute(ParallelFlow.Builder.aNewParallelFlow()
		        		.execute(psm2AsmWork, psm2MeasureWork)
		        		.build())
		        .then(ParallelFlow.Builder.aNewParallelFlow()
		        		.execute(asm2RdbmsWork, asm2OpenapiWork)
		        		.build())
		        .then(SequentialFlow.Builder.aNewSequentialFlow()
		        		.execute(rdbms2LiquibaseWork)
		        		.build())
		        .build();
		
		 WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
	     workFlowEngine.run(workflow);
	
	}
	
	
}

