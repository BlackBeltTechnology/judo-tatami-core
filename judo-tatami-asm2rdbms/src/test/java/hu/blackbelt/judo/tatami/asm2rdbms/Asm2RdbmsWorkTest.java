package hu.blackbelt.judo.tatami.asm2rdbms;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;


public class Asm2RdbmsWorkTest {
	
	public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String MODEL_DIRECTORY = "model";
    
    Asm2RdbmsWork asm2RdbmsWork;
    TransformationContext transformationContext;
    
    @BeforeEach
    void setUp() throws IOException, AsmModel.AsmValidationException
    {
    	AsmModel asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL))
                .name(NORTHWIND));

        transformationContext = new TransformationContext(NORTHWIND);
        transformationContext.put(asmModel);
        transformationContext.put(Asm2RdbmsWork.RDBMS_EXCELMODEL_URI, new File(MODEL_DIRECTORY).toURI());
        transformationContext.put(Asm2RdbmsWork.RDBMS_DIALECT, "hsqldb");
        
        asm2RdbmsWork = new Asm2RdbmsWork(transformationContext, new File(TARGET_TEST_CLASSES,"epsilon/transformations").toURI());
    }
   
    @Test
    void testSimpleWorkflow()
    {
    	WorkFlow workflow = aNewSequentialFlow()
                .execute(asm2RdbmsWork)
                .build();

        WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
        WorkReport workReport = workFlowEngine.run(workflow);

        assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
    }

}
