package hu.blackbelt.judo.tatami.asm2openapi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class Asm2OpenAPIWorkTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Asm2OpenAPIWork asm2OpenAPIWork;
    TransformationContext transformationContext;

    @BeforeEach
    void setUp() throws IOException, AsmModel.AsmValidationException {
        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        AsmModel asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL))
                .name(NORTHWIND));

        transformationContext = new TransformationContext(NORTHWIND);
        transformationContext.put(asmModel);

        asm2OpenAPIWork = new Asm2OpenAPIWork(transformationContext, new File("src/main/epsilon/transformations/openapi").toURI());
    }

    @Test
    void testSimpleWokflow() {
        WorkFlow workflow = aNewSequentialFlow()
                .execute(asm2OpenAPIWork)
                .build();

        WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
        WorkReport workReport = workFlowEngine.run(workflow);

        assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
    }


}