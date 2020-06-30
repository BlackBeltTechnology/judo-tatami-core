package hu.blackbelt.judo.tatami.openapi2restclient;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.model.northwind.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.buildExpressionModel;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.buildMeasureModel;
import static hu.blackbelt.judo.tatami.asm2expression.Asm2Expression.executeAsm2Expression;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;

@Slf4j
public class Openapi2RestClientTest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    OpenapiModel openapiModel;
    TransformationContext transformationContext;

    @BeforeEach
    void setUp() throws Exception {
        Demo demo = new Demo();
        PsmModel psmModel = demo.fullDemo();

        AsmModel asmModel = buildAsmModel().name(NORTHWIND).build();
        executePsm2AsmTransformation(psmModel, asmModel);

        MeasureModel measureModel = buildMeasureModel().name(NORTHWIND).build();
        executePsm2MeasureTransformation(psmModel, measureModel);

        ExpressionModel expressionModel = buildExpressionModel().name(NORTHWIND).build();
        executeAsm2Expression(asmModel, measureModel, expressionModel);

        openapiModel = OpenapiModel.buildOpenapiModel().name(NORTHWIND).build();
        executeAsm2OpenAPITransformation(asmModel, openapiModel);

        transformationContext = new TransformationContext(NORTHWIND);
        transformationContext.put(expressionModel);
        transformationContext.put(psmModel);
        transformationContext.put(openapiModel);
        transformationContext.put(measureModel);
    }



    @Test
    public void testExecuteAsm2SDKGeneration() throws Exception {

        Map<API, Map<String, File>>  generated = hu.blackbelt.judo.tatami.openapi2restclient.Openapi2RestClient.executeOpenaapi2RestClientGeneration(
                openapiModel, "dart2-api", new File(TARGET_TEST_CLASSES), ImmutableMap.of("enablePostProcessFile", "true"));
    }
}
