package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableMap;
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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.*;

@Slf4j
public class Asm2OpenAPITest {

    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String OPENAPI_NORTHWIND = "openapi:northwind";
    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String NORTHWIND_OPENAPI_MODEL = "northwind-openapi.model";
    public static final String NORTHWIND_ASM_2_OPENAPI_MODEL = "northwind-asm2openapi.model";
    public static final String EPSILON_TRANSFORMATIONS_OPENAPI = "epsilon/transformations/openapi";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    public static final URI NORTHWIND_ASM_2_OPENAPI_URI =
            URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_2_OPENAPI_MODEL).getAbsolutePath());

    Log slf4jlog;
    AsmModel asmModel;
    OpenapiModel openapiModel;

    @Before
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .uri(URI.createURI(ASM_NORTHWIND))
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL))
                .name(NORTHWIND));

        // Create empty OPENAPI model
        openapiModel = OpenapiModel.buildOpenapiModel()
                .name(NORTHWIND)
                .uri(URI.createURI(OPENAPI_NORTHWIND))
                .build();
    }

    @Test
    public void testAsm2OpenAPITransformation() throws Exception {

        Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(
                asmModel,
                openapiModel,
                new Slf4jLog(log),
                new File(TARGET_TEST_CLASSES, EPSILON_TRANSFORMATIONS_OPENAPI).toURI());

        // Saving trace map
        ResourceSet traceSavedResourceSet = createAsm2OpenAPITraceResourceSet();
        Resource traceResoureSaved = traceSavedResourceSet.createResource(NORTHWIND_ASM_2_OPENAPI_URI);
        traceResoureSaved.getContents().addAll(getAsm2OpenAPITrace(asm2OpenAPITransformationTrace.getTrace()));
        traceResoureSaved.save(ImmutableMap.of());

        // Loading trace map
        ResourceSet traceLoadedResourceSet = createAsm2OpenAPITraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(NORTHWIND_ASM_2_OPENAPI_URI);
        traceResoureLoaded.load(ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveAsm2OpenAPITrace(traceResoureLoaded, asmModel, openapiModel);

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
