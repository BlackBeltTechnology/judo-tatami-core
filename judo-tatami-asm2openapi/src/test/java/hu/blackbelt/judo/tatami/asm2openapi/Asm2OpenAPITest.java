package hu.blackbelt.judo.tatami.asm2openapi;

import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace.fromModelsAndTrace;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.model.northwind.Demo;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2OpenAPITest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_OPENAPI_MODEL = "northwind-openapi.model";
    public static final String NORTHWIND_ASM_2_OPENAPI_MODEL = "northwind-asm2openapi.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    AsmModel asmModel;
    OpenapiModel openapiModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        PsmModel psmModel = new Demo().fullDemo();

        // Create empty ASM model
        asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();
        
        executePsm2AsmTransformation(psmModel, asmModel);

        // Create empty OPENAPI model
        openapiModel = OpenapiModel.buildOpenapiModel()
                .name(NORTHWIND)
                .build();
    }

    @Test
    public void testAsm2OpenAPITransformation() throws Exception {

        Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(asmModel,
                openapiModel);

        // Saving trace map
        asm2OpenAPITransformationTrace.save(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_2_OPENAPI_MODEL));


        // Loading trace map
        Asm2OpenAPITransformationTrace asm2OpenAPITransformationTraceLoaded =
                fromModelsAndTrace(NORTHWIND, asmModel, openapiModel, new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_2_OPENAPI_MODEL));


        Map<EObject, List<EObject>> resolvedTrace =
                asm2OpenAPITransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace("{} -> {}", e, t);
            }
        }

        openapiModel.saveOpenapiModel(openapiSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_OPENAPI_MODEL)));


        // Save JSON and YAML Swagger files
        openapiModel.getResource().getContents().stream()
                .filter(m -> m instanceof API).map(m -> (API) m)
                .forEach(m -> {
                    final Swagger swagger = OpenAPIExporter.convertModelToOpenAPI((API) m);

                    final String title = ((API) m).getInfo().getTitle();
                    final File swaggerJsonFile = new File(TARGET_TEST_CLASSES, "northwind-openapi-" + title + ".json");
                    try (final Writer targetFileWriter = new FileWriter(swaggerJsonFile)) {
                        final String json = Json.pretty().writeValueAsString(swagger);
                        targetFileWriter.append(json);
                        log.trace(json);
                    } catch (IOException ex) {
                        log.error("Unable to create JSON output", ex);
                    }
                    final File swaggerYamlFile = new File(TARGET_TEST_CLASSES,"northwind-openapi-" + title + ".yaml");
                    try (final Writer targetFileWriter = new FileWriter(swaggerYamlFile)) {
                        final String yaml = Yaml.pretty().writeValueAsString(swagger);
                        targetFileWriter.append(yaml);
                        log.trace(yaml);
                    } catch (IOException ex) {
                        log.error("Unable to create YAML output", ex);
                    }
                });
    }
}
