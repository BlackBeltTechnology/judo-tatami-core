package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Context;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.tatami.ui2client.GeneratorTemplate.TemplateEvaulator;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Ui2Client {

    public static final Log log = new Slf4jLog(LoggerFactory.getLogger(Ui2FlutterClient.class));

    public static final String TEMPLATE_ROOT_TATAMI_UI_2_CLIENT = "templates/";
    public static final String NAME = "name";

    public static Map<Application, Collection<GeneratedFile>> executeUi2ClientGenerationByApplication(ClientGenerator clientGenerator) throws Exception {
        return executeUi2ClientGenerationByApplication(clientGenerator, (Application a) -> true, log);
    }

    public static Map<Application, Collection<GeneratedFile>> executeUi2ClientGenerationByApplication(ClientGenerator clientGenerator, Log log) throws Exception {
        return executeUi2ClientGenerationByApplication(clientGenerator, (Application a) -> true, log);
    }

    public static Map<Application, Collection<GeneratedFile>> executeUi2ClientGenerationByApplication(ClientGenerator clientGenerator, Predicate<Application> applicationPredicate, Log log) throws Exception {

        Map<Application, Collection<GeneratedFile>> sourcesByApplication = new HashMap<>();
        clientGenerator.getModelResourceSupport().getStreamOfUiApplication().forEach(app -> { sourcesByApplication.put(app, new HashSet<>()); });

        Set<Application> applications = clientGenerator.getModelResourceSupport().getStreamOfUiApplication().filter(applicationPredicate).collect(Collectors.toSet());

        for (GeneratorTemplate generatorTemplate : clientGenerator.getGeneratorTemplates()) {
            if (generatorTemplate.getFactoryExpression() != null) {
                TemplateEvaulator templateEvaulator = generatorTemplate.getTemplateEvalulator(clientGenerator);

                if (templateEvaulator.getTemplate() != null || generatorTemplate.isCopy()) {
                   applications.forEach(application -> {
                        clientGenerator.setVariable("applications", applications);
                        clientGenerator.setVariable("application", application);
                        clientGenerator.setVariable("template", generatorTemplate);

                        templateEvaulator.getFactoryExpressionResult(application, Collection.class).stream().forEach(element -> {
                            clientGenerator.setVariable("self", element);

                            Context.Builder contextBuilder = Context
                                    .newBuilder(element)
                                    .combine("application", application)
                                    .combine("template", generatorTemplate);

                            generatorTemplate.evalToContextBuilder(templateEvaulator, contextBuilder);
                            GeneratedFile generatedFile = generateFile(clientGenerator, templateEvaulator, generatorTemplate, contextBuilder, log);
                            sourcesByApplication.get(application).add(generatedFile);
                        });

                    });
                }
            }
        }
        return sourcesByApplication;
    }


    public static Collection<GeneratedFile> executeUi2ClientGeneration(ClientGenerator clientGenerator, Predicate<Application> applicationPredicate, Log log) throws Exception {

        Collection<GeneratedFile> sources = new HashSet<>();

        Set<Application> applications = clientGenerator.getModelResourceSupport().getStreamOfUiApplication().filter(applicationPredicate).collect(Collectors.toSet());

        for (GeneratorTemplate generatorTemplate : clientGenerator.getGeneratorTemplates()) {
            if (generatorTemplate.getFactoryExpression() != null) {
                TemplateEvaulator templateEvaulator = generatorTemplate.getTemplateEvalulator(clientGenerator);

                if (templateEvaulator.getTemplate() != null || generatorTemplate.isCopy()) {
                    clientGenerator.setVariable("applications", applications);
                    clientGenerator.setVariable("template", generatorTemplate);

                    templateEvaulator.getFactoryExpressionResult(applications, Collection.class).stream().forEach(element -> {
                        clientGenerator.setVariable("self", element);

                        Context.Builder contextBuilder = Context
                                .newBuilder(element)
                                .combine("applications", applications)
                                .combine("template", generatorTemplate);

                        generatorTemplate.evalToContextBuilder(templateEvaulator, contextBuilder);
                        GeneratedFile generatedFile = generateFile(clientGenerator, templateEvaulator, generatorTemplate, contextBuilder, log);
                        sources.add(generatedFile);
                    });
                }
            }
        }
        return sources;
    }


    private static GeneratedFile generateFile(final ClientGenerator clientGenerator,
                                              final TemplateEvaulator templateEvaulator,
                                              final GeneratorTemplate generatorTemplate,
                                              final Context.Builder contextBuilder,
                                              final Log log) {

        GeneratedFile generatedFile = new GeneratedFile();
        generatedFile.setOverwrite(templateEvaulator.getOverWriteExpression().getValue(clientGenerator.getSpelEvaulationContext(), Boolean.class));
        generatedFile.setPath(templateEvaulator.getPathExpression().getValue(clientGenerator.getSpelEvaulationContext(), String.class));

        if (generatorTemplate.isCopy()) {
            String location = generatorTemplate.getTemplateName();
            if (location.startsWith("/")) {
                location =  location.substring(1);
            }
            location = clientGenerator.getScriptDirectoryTemplateLoader().resolve(location);
            try {
                URL resource = clientGenerator.getScriptDirectoryTemplateLoader().getResource(location);
                if (resource != null) {
                    generatedFile.setContent(ByteStreams.toByteArray(resource.openStream()));
                }  else {
                    log.error("Could not locate: " + location);
                }
            } catch (IOException e) {
                log.error("Could not resolve: " + location);
            }
        } else {
            StringWriter sourceFile = new StringWriter();
            try {
                templateEvaulator.getTemplate().apply(contextBuilder.build(), sourceFile);
            } catch (IOException e) {
                log.error("Could not generate template: " + generatedFile.getPath());
            }
            generatedFile.setContent(sourceFile.toString().getBytes(Charsets.UTF_8));
        }
        return generatedFile;
    }


    public static Map<Application, InputStream> executeUi2ClientGenerationAsZip(ClientGenerator clientGenerator, Collection<GeneratorTemplate> generatorTemplates) throws Exception {
        return executeUi2ClientGenerationAsZip(clientGenerator, log);
    }

    public static Map<Application, InputStream> executeUi2ClientGenerationAsZip(ClientGenerator clientGenerator, Log log) throws Exception {
        return executeUi2ClientGenerationByApplication(clientGenerator, log).entrySet()
                .stream().collect(Collectors.toMap(e -> e.getKey(), e -> getGeneratedFilesAsZip(e.getValue())));
    }

    public static Consumer<Map.Entry<Application, Collection<GeneratedFile>>> getDirectoryWriter(File directory, Function<Application, String> outputNameFunction, Log log) {
        return e -> {
            File output = new File(directory, outputNameFunction.apply(e.getKey()));
            e.getValue().stream().forEach(f -> {
                File outFile = new File(output, f.getPath());
                outFile.getParentFile().mkdirs();
                if (!outFile.exists() || (f.getOverwrite())) {
                    try {
                        ByteStreams.copy(new ByteArrayInputStream(f.getContent()), new FileOutputStream(outFile));
                    } catch (IOException ioException) {
                        log.error("Could not write file: " + outFile.getAbsolutePath(), ioException);
                    }
                }
            });
        };
    }
    public static void executeUi2ClientGenerationToDirectory(ClientGenerator clientGenerator, File directory, Function<Application, String> outputNameGenerator) throws Exception {
        executeUi2ClientGenerationByApplication(clientGenerator, log).entrySet()
                .stream().forEach(getDirectoryWriter(directory, outputNameGenerator, log));
    }

    public static void executeUi2ClientGenerationToDirectory(ClientGenerator clientGenerator, File directory, Function<Application, String> outputNameGenerator, Log log) throws Exception {
        executeUi2ClientGenerationByApplication(clientGenerator, log).entrySet()
                .stream().forEach(getDirectoryWriter(directory, outputNameGenerator, log));

    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateUi2ClientTemplateScriptURI() {
        URI uiRoot = Ui2Client.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (uiRoot.toString().endsWith(".jar")) {
            uiRoot = new URI("jar:" + uiRoot.toString() + "!/" + TEMPLATE_ROOT_TATAMI_UI_2_CLIENT);
        } else if (uiRoot.toString().startsWith("jar:bundle:")) {
            uiRoot = new URI(uiRoot.toString().substring(4, uiRoot.toString().indexOf("!")) + TEMPLATE_ROOT_TATAMI_UI_2_CLIENT);
        } else {
            uiRoot = new URI(uiRoot.toString() + "/" + TEMPLATE_ROOT_TATAMI_UI_2_CLIENT);
        }
        return uiRoot;
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateUi2ClientTemplateScriptURI(String template) {
        URI uiRoot = Ui2Client.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (uiRoot.toString().endsWith(".jar")) {
            uiRoot = new URI("jar:" + uiRoot.toString() + "!/" + TEMPLATE_ROOT_TATAMI_UI_2_CLIENT + template);
        } else if (uiRoot.toString().startsWith("jar:bundle:")) {
            uiRoot = new URI(uiRoot.toString().substring(4, uiRoot.toString().indexOf("!")) + TEMPLATE_ROOT_TATAMI_UI_2_CLIENT + template);
        } else {
            uiRoot = new URI(uiRoot.toString() + "/" + TEMPLATE_ROOT_TATAMI_UI_2_CLIENT + template);
        }
        return uiRoot;
    }

    @SneakyThrows(IOException.class)
    public static InputStream getGeneratedFilesAsZip(Collection<GeneratedFile> generatedFiles) {
        ByteArrayOutputStream generatedZip = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(generatedZip);
        for (GeneratedFile generatedFile : generatedFiles) {
            zipOutputStream.putNextEntry(new ZipEntry(generatedFile.getPath()));
            zipOutputStream.write(generatedFile.getContent(), 0, generatedFile.getContent().length);
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
        }
        zipOutputStream.flush();
        zipOutputStream.close();
        return new ByteArrayInputStream(generatedZip.toByteArray());
    }

    @Deprecated
    // TODO: Compatibiliyu reasons
    public static void main(String[] args) throws Exception {
        Ui2FlutterClient.main(args);
    }
}
