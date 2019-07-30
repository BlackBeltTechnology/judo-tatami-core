package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.uoc.som.openapi.API;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
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

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.createOpenAPIResourceSet;
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
    public static final String VERSION = "1.0.0";

    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), targetDir().getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM),
                        URI.createURI(OPENAPI_NORTHWIND), URI.createURI(URN_NORTHWIND_OPENAPI))
        );
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler);
        asmModel = AsmModelLoader.loadAsmModel(
                asmResourceSet,
                URI.createURI(ASM_NORTHWIND),
                NORTHWIND,
                VERSION);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testAsm2OpenAPITransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet openAPIResourceSet = createOpenAPIResourceSet(uriHandler);


        OpenAPIModel openAPIModel = OpenAPIModel.buildOpenAPIModel()
                .name(asmModel.getName())
                .resourceSet(openAPIResourceSet)
                .uri(URI.createURI(OPENAPI_NORTHWIND))
                .version(asmModel.getVersion())
                .build();

        Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(openAPIResourceSet, asmModel, openAPIModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations/openapi").toURI());

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getAsm2OpenAPITrace(asm2OpenAPITransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(targetDir().getAbsolutePath(), ASM_2_OPENAPI_MODEL)), ImmutableMap.of());

        // Loading trace map
        ResourceSet traceLoadedResourceSet = createAsm2OpenAPITraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_JQLEXTRACT_2_EXPRESSION));
        traceResoureLoaded.load(new FileInputStream(new File(targetDir().getAbsolutePath(), ASM_2_OPENAPI_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveAsm2OpenAPITrace(traceResoureLoaded, asmModel, openAPIModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace("{} -> {}", e, t);
            }
        }

        OpenAPIModelLoader.saveOpenAPIModel(openAPIModel);

        // Save JSON and YAML Swagger files
        openAPIModel.getResourceSet().getResource(openAPIModel.getUri(), false).getContents().stream()
                .filter(m -> m instanceof API).map(m -> (API) m)
                .forEach(m -> {
                    final Swagger swagger = OpenAPIExporter.convertModelToOpenAPI((API) m);

                    final String title = ((API) m).getInfo().getTitle();
                    final File swaggerJsonFile = new File(targetDir().getAbsolutePath() + "/northwind-openapi-" + title + ".json");
                    try (final Writer targetFileWriter = new FileWriter(swaggerJsonFile)) {
                        final String json = Json.pretty().writeValueAsString(swagger);
                        targetFileWriter.append(json);
                        log.trace(json);
                    } catch (IOException ex) {
                        log.error("Unable to create JSON output", ex);
                    }
                    final File swaggerYamlFile = new File(targetDir().getAbsolutePath() + "/northwind-openapi-" + title + ".yaml");
                    try (final Writer targetFileWriter = new FileWriter(swaggerYamlFile)) {
                        final String yaml = Yaml.pretty().writeValueAsString(swagger);
                        targetFileWriter.append(yaml);
                        log.trace(yaml);
                    } catch (IOException ex) {
                        log.error("Unable to create YAML output", ex);
                    }
                });
    }


    public File targetDir() {
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}
