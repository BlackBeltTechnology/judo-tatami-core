package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.meta.openapi.support.OpenapiModelResourceSupport;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.*;

@Slf4j
public class Asm2OpenAPITest {

    public static final String ASM_2_OPENAPI_MODEL = "asm2openapi.model";
    public static final String TRACE_JQLEXTRACT_2_EXPRESSION = "trace:asm2openapi";
    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String OPENAPI_NORTHWIND = "openapi:northwind";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String URN_NORTHWIND_OPENAPI = "urn:northwind-openapi.model";
    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";


    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;
    OpenapiModel openapiModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), TARGET_TEST_CLASSES)),
                ImmutableMap.of(
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM),
                        URI.createURI(OPENAPI_NORTHWIND), URI.createURI(URN_NORTHWIND_OPENAPI))
        );
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = AsmModel.loadAsmModel(AsmModel.LoadArguments.loadArgumentsBuilder()
                .uri(URI.createURI(ASM_NORTHWIND))
                .uriHandler(Optional.of(uriHandler))
                .name(NORTHWIND)
                .build());

        // Create empty OPENAPI model
        OpenapiModelResourceSupport openapiModelResourceSupport = OpenapiModelResourceSupport.openapiModelResourceSupportBuilder()
                .uriHandler(Optional.of(uriHandler))
                .build();

        openapiModel = OpenapiModel.buildOpenapiModel()
                .openapiModelResourceSupport(openapiModelResourceSupport)
                .name(NORTHWIND)
                .uri(URI.createURI(OPENAPI_NORTHWIND))
                .build();
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testAsm2OpenAPITransformation() throws Exception {

        Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(
                openapiModel.getResourceSet(),
                asmModel,
                openapiModel,
                new Slf4jLog(log),
                new File(TARGET_TEST_CLASSES, "epsilon/transformations/openapi").toURI());

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getAsm2OpenAPITrace(asm2OpenAPITransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(TARGET_TEST_CLASSES, ASM_2_OPENAPI_MODEL)), ImmutableMap.of());

        // Loading trace map
        ResourceSet traceLoadedResourceSet = createAsm2OpenAPITraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_JQLEXTRACT_2_EXPRESSION));
        traceResoureLoaded.load(new FileInputStream(new File(TARGET_TEST_CLASSES, ASM_2_OPENAPI_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveAsm2OpenAPITrace(traceResoureLoaded, asmModel, openapiModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace("{} -> {}", e, t);
            }
        }

        openapiModel.saveOpenapiModel();

        // Save JSON and YAML Swagger files
        openapiModel.getResourceSet().getResource(openapiModel.getUri(), false).getContents().stream()
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
