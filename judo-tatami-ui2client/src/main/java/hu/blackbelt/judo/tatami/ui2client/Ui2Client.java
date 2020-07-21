package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.io.*;
import com.google.common.base.Charsets;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.LoadArguments.uiLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.loadUi;

@Slf4j
public class Ui2Client {

    public static final String SCRIPT_ROOT_TATAMI_UI_2_FLUTTER = "tatami/ui2flutter/templates/";

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel) throws Exception {
        return executeUi2FlutterGeneration(uiModel, Collections.EMPTY_LIST, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI());
    }

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Log log) throws Exception {
        return executeUi2FlutterGeneration(uiModel, Collections.EMPTY_LIST, log, calculateUi2FlutterTemplateScriptURI());
    }

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates) throws Exception {
        return executeUi2FlutterGeneration(uiModel, generatorTemplates, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI());
    }

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log) throws Exception {
        return executeUi2FlutterGeneration(uiModel, generatorTemplates, log, calculateUi2FlutterTemplateScriptURI());
    }

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log,
                                                            URI scriptDir) throws Exception {

        /*
        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("UI")
                                .resource(uiModel.getResourceSet().getResource(uiModel.getUri(), false))
                                .build()
                        )
                )
                .build();

        // run the model / metadata loading
        executionContext.load();

        EglExecutionContext eglExecutionContext = eglExecutionContextBuilder()
                .source(UriUtil.resolve("main.egl", scriptDir))
                .outputRoot(File.createTempFile("ui2client", String.valueOf(System.currentTimeMillis())).getAbsolutePath())
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(uiModel.getVersion()).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(eglExecutionContext); */


        /*
        ClassPathTemplateLoader cpTemplateLoader = new ClassPathTemplateLoader();
        Handlebars handlebars = new Handlebars();
        TemplateLoader loader = cpTemplateLoader;

        if (scriptDir.getScheme().equals("file")) {
            FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(scriptDir));
            fileTemplateLoader.setSuffix(".dart.hbs");
            loader = new CompositeTemplateLoader(fileTemplateLoader, cpTemplateLoader);
        } */

        TemplateLoader scriptDirectoryTemplateLoader = new ClientGeneratorTemplateLoader(scriptDir);
        scriptDirectoryTemplateLoader.setSuffix(".hbs");

        TemplateLoader defaultEmbeddedTemplateLoader = new ClassPathTemplateLoader();

        Handlebars handlebars = new Handlebars();
        handlebars.with(scriptDirectoryTemplateLoader);
        handlebars.setStringParams(true);
        handlebars.setCharset(Charsets.UTF_8);

        //
        //        Template template = handlebars.compile(templateName);
        //        return template;
        // HighConcurrencyTemplateCache

        UiModelResourceSupport modelResourceSupport = loadUi(uiLoadArgumentsBuilder().resourceSet(uiModel.getResourceSet()).build());
        modelResourceSupport.
        for (GeneratorTemplate generatorTemplate : generatorTemplates) {

        }



        // Compiling templates

        Set<GeneratedFile> sourceFiles = (Set<GeneratedFile>) executionContext.getContext().get("outputGeneratedSources");

        // compile(outputDir, (Set<String>)executionContext.getContext().get("outputJavaClasses"));
        executionContext.commit();
        executionContext.close();

        return sourceFiles;

    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel) throws Exception {
        return getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, Collections.EMPTY_LIST, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI()));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Log log) throws Exception {
        return getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, Collections.EMPTY_LIST, log, calculateUi2FlutterTemplateScriptURI()));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Log log, URI scriptDir) throws Exception {
        return  getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, Collections.EMPTY_LIST, log, scriptDir));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates) throws Exception {
        return getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, generatorTemplates, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI()));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log) throws Exception {
        return getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, generatorTemplates, log, calculateUi2FlutterTemplateScriptURI()));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log, URI scriptDir) throws Exception {
        return  getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, generatorTemplates, log, scriptDir));
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateUi2FlutterTemplateScriptURI() {
        URI uiRoot = Ui2Client.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (uiRoot.toString().endsWith(".jar")) {
            uiRoot = new URI("jar:" + uiRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_UI_2_FLUTTER);
        } else if (uiRoot.toString().startsWith("jar:bundle:")) {
            uiRoot = new URI(uiRoot.toString().substring(4, uiRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_UI_2_FLUTTER);
        } else {
            uiRoot = new URI(uiRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_UI_2_FLUTTER);
        }
        return uiRoot;
    }

    public static InputStream getGeneratedFilesAsZip(Collection<GeneratedFile> generatedFiles) throws IOException {
        ByteArrayOutputStream generatedZip = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(generatedZip);
        for (GeneratedFile generatedFile : generatedFiles) {
            zipOutputStream.putNextEntry(new ZipEntry(generatedFile.getPath()));
            byte[] bytes = generatedFile.getSource().getBytes("utf-8");
            zipOutputStream.write(bytes, 0, bytes.length);
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
        }
        zipOutputStream.flush();
        zipOutputStream.close();
        return new ByteArrayInputStream(generatedZip.toByteArray());
    }
}
