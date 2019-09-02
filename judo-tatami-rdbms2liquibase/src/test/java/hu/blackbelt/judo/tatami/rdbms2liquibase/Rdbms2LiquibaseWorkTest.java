package hu.blackbelt.judo.tatami.rdbms2liquibase;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.LoadArguments.rdbmsLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.loadRdbmsModel;

import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

public class Rdbms2LiquibaseWorkTest {
	
	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_RDBMS_MODEL = "northwind-rdbms.model";
    
    Rdbms2LiquibaseWork rdbms2LiquibaseWork;
    TransformationContext transformationContext;
    
    @BeforeEach
    void setUp() throws IOException, RdbmsModel.RdbmsValidationException
    {
    	RdbmsModel rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(NORTHWIND)
                .build();
    	
    	transformationContext = new TransformationContext(NORTHWIND);
    	transformationContext.put(rdbmsModel);
    	transformationContext.put(Rdbms2LiquibaseWork.LIQUIBASE_DIALECT,"hsqldb");
    	
    	rdbms2LiquibaseWork = new Rdbms2LiquibaseWork(transformationContext,new File("src/main/epsilon/transformations").toURI());
    }
    
    @Test
    void testSimpleWorkflow()
    {
    	WorkFlow workflow = aNewSequentialFlow()
                .execute(rdbms2LiquibaseWork)
                .build();

        WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
        WorkReport workReport = workFlowEngine.run(workflow);

        assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
    }

}
