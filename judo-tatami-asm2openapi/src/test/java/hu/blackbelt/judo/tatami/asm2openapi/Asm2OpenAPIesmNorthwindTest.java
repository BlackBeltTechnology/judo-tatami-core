package hu.blackbelt.judo.tatami.asm2openapi;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.calculateAsm2OpenapiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;

@Slf4j
public class Asm2OpenAPIesmNorthwindTest {
    public static final String MODEL_NAME = "northwind";
    public static final String ESM_NORTHWIND_ASM_MODEL = "esmNorthwind-asm.model";
    public static final String ESM_NORTHWIND_OPENAPI_MODEL = "esmNorthwind-openapi.model";
    public static final String ESM_NORTHWIND_ASM_2_OPENAPI_MODEL = "psmNorthwind-asm2openapi.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    AsmModel asmModel;
    OpenapiModel openapiModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, ESM_NORTHWIND_ASM_MODEL))
                .name(MODEL_NAME));

        // Create empty OPENAPI model
        openapiModel = OpenapiModel.buildOpenapiModel()
                .name(MODEL_NAME)
                .build();
    }

    @Test
    public void testNorthwindModel() throws Exception {
        slf4jlog.info("~~~~~testEsmNorthwind");
        Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(
                asmModel,
                openapiModel,
                new Slf4jLog(log),
                calculateAsm2OpenapiTransformationScriptURI());

        openapiModel.saveOpenapiModel(openapiSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, ESM_NORTHWIND_OPENAPI_MODEL)));

        // Save JSON and YAML Swagger files
        openapiModel.getResource().getContents().stream()
                .filter(m -> m instanceof API).map(m -> (API) m)
                .forEach(m -> {
                    final Swagger swagger = OpenAPIExporter.convertModelToOpenAPI((API) m);

                    final String title = ((API) m).getInfo().getTitle();
                    final File swaggerJsonFile = new File(TARGET_TEST_CLASSES, "esmNorthwind-openapi-" + title + ".json");
                    try (final Writer targetFileWriter = new FileWriter(swaggerJsonFile)) {
                        final String json = Json.pretty().writeValueAsString(swagger);
                        targetFileWriter.append(json);
                        log.trace(json);
                    } catch (IOException ex) {
                        log.error("Unable to create JSON output", ex);
                    }
                    final File swaggerYamlFile = new File(TARGET_TEST_CLASSES,"esmNorthwind-openapi-" + title + ".yaml");
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
