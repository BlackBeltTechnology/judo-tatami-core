package hu.blackbelt.judo.tatami.openapi2restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.tatami.core.ZipUtil;
import io.swagger.parser.OpenAPIParser;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.converter.SwaggerConverter;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.*;

import java.io.*;
import java.net.URI;
import java.util.*;

import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@Slf4j
public class Openapi2RestClient {

    public static Map<API, Map<String, File>> executeOpenaapi2RestClientGeneration(OpenapiModel openapiModel, String language, File generationOutputDir, Map<String, Object> generationOptions) throws Exception {
        return executeOpenaapi2RestClientGeneration(openapiModel, language, new Slf4jLog(log), generationOutputDir, generationOptions);
    }

    public static Map<API, Map<String, File>> executeOpenaapi2RestClientGeneration(OpenapiModel openapiModel, String language, Log log,
                                                                                   File generationOutputDir, Map<String, Object> generationOptions) throws Exception {

        Map<API, Map<String, File>> generatedResources = new HashMap<>();
        generationOutputDir.mkdirs();

        openapiModel.getResource().getContents().stream()
                .filter(m -> m instanceof API).map(m -> (API) m)
                .forEach(m -> {
                    generatedResources.put(m, new HashMap<>());
                    generatedResources.get(m).put("dart2-api", generate(m, language, generationOptions, generationOutputDir));
                });
        return generatedResources;
    }


    private static File generate(API api, String language, Map<String, Object> opts, File outputFolder) {
        log.debug(String.format(Locale.ROOT, "generate client for %s", language));

        OpenAPI openapi;
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);

        List<AuthorizationValue> authorizationValues = new ArrayList<>();
        try {
            openapi = new SwaggerConverter().readContents(Json.mapper().writeValueAsString(OpenAPIExporter.convertModelToOpenAPI(api)),
                    authorizationValues, parseOptions).getOpenAPI();
        } catch (Exception e) {
            throw new RuntimeException("Could not load swagger definition", e);
        }

        String generationFolder = getTmpFolder().getAbsolutePath() + File.separator + api.getInfo().getTitle() + "-" + language;
        String outputFilename = outputFolder + File.separator + api.getInfo().getTitle() + "-" + language + "-bundle.zip";

        CodegenConfig codegenConfig;
        codegenConfig = CodegenConfigLoader.forName(language);
        codegenConfig.additionalProperties().putAll(opts);
        codegenConfig.additionalProperties().put("openAPI", openapi);
        codegenConfig.setOutputDir(generationFolder);

        ClientOptInput clientOptInput = new ClientOptInput();
        clientOptInput.openAPI(openapi);
        clientOptInput.config(codegenConfig);

        try {
            List<File> files = new DefaultGenerator().opts(clientOptInput).generate();
            if (files.size() > 0) {
                List<File> filesToAdd = new ArrayList<>();
                log.debug("adding to " + generationFolder);
                filesToAdd.add(new File(generationFolder));
                ZipUtil zip = new ZipUtil();
                zip.compressFiles(filesToAdd, outputFilename);
            } else {
                throw new RuntimeException("A target generation was attempted, but no files were created!");
            }
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    log.error("unable to delete file " + file.getAbsolutePath(), e);
                }
            }
            try {
                new File(generationFolder).delete();
            } catch (Exception e) {
                log.error("unable to delete output folder " + generationFolder, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to build target: " + e.getMessage(), e);
        }
        return new File(outputFilename);
    }

    private static File getTmpFolder() {
        try {
            File outputFolder = File.createTempFile("codegen-", "-tmp");
            outputFolder.delete();
            outputFolder.mkdir();
            outputFolder.deleteOnExit();
            return outputFolder;
        } catch (Exception e) {
            throw new RuntimeException("Cannot access tmp folder", e);
        }
    }
}


