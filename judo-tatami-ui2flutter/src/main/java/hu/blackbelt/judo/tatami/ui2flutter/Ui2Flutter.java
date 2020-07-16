package hu.blackbelt.judo.tatami.ui2flutter;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.util.UriUtil;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext.eglExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

@Slf4j
public class Ui2Flutter {

    public static final String SCRIPT_ROOT_TATAMI_UI_2_FLUTTER = "tatami/ui2flutter/templates/";

    public static Set<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel) throws Exception {
        return executeUi2FlutterGeneration(uiModel, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI());
    }

    public static Set<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Log log) throws Exception {
        return executeUi2FlutterGeneration(uiModel, log, calculateUi2FlutterTemplateScriptURI());
    }

    public static Set<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Log log,
                                                            URI scriptDir) throws Exception {

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
                .outputRoot(File.createTempFile("ui2flutter", String.valueOf(System.currentTimeMillis())).getAbsolutePath())
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(uiModel.getVersion()).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(eglExecutionContext);

        Set<GeneratedFile> sourceFiles = (Set<GeneratedFile>)executionContext.getContext().get("outputGeneratedSources");

        // compile(outputDir, (Set<String>)executionContext.getContext().get("outputJavaClasses"));
        executionContext.commit();
        executionContext.close();

        return sourceFiles;

    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel) throws Exception {
        return getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI()));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Log log) throws Exception {
        return getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, log, calculateUi2FlutterTemplateScriptURI()));
    }

    public static InputStream executeUi2FlutterGenerationAsZip(UiModel uiModel, Log log, URI scriptDir) throws Exception {
        return  getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel, log, scriptDir));
    }

        @SneakyThrows(URISyntaxException.class)
    public static URI calculateUi2FlutterTemplateScriptURI() {
        URI uiRoot = Ui2Flutter.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (uiRoot.toString().endsWith(".jar")) {
            uiRoot = new URI("jar:" + uiRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_UI_2_FLUTTER);
        } else if (uiRoot.toString().startsWith("jar:bundle:")) {
            uiRoot = new URI(uiRoot.toString().substring(4, uiRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_UI_2_FLUTTER);
        } else {
            uiRoot = new URI(uiRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_UI_2_FLUTTER);
        }
        return uiRoot;
    }

    public static InputStream getGeneratedFilesAsZip(Set<GeneratedFile> generatedFiles) throws IOException {
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
