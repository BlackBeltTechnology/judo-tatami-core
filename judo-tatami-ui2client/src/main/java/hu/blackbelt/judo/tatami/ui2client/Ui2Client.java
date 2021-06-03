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
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
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

        Map<Application, Collection<GeneratedFile>> sourcesByApplication = new ConcurrentHashMap<>();
        clientGenerator.getModelResourceSupport().getStreamOfUiApplication().forEach(app -> { sourcesByApplication.put(app, ConcurrentHashMap.newKeySet()); });

        Set<Application> applications = clientGenerator.getModelResourceSupport().getStreamOfUiApplication().filter(applicationPredicate).collect(Collectors.toSet());

        List<CompletableFuture<GeneratedFile>> tasks = new ArrayList<>();
        clientGenerator.getGeneratorTemplates().stream().forEach(generatorTemplate -> {

            StandardEvaluationContext evaulationContext = clientGenerator.createSpringEvaulationContext();
            if (generatorTemplate.getFactoryExpression() != null) {
                final TemplateEvaulator templateEvaulator;
                try {
                    templateEvaulator = generatorTemplate.getTemplateEvalulator(clientGenerator, evaulationContext);
                } catch (IOException e) {
                    throw new RuntimeException("Could not evaulate template", e);
                }

                if (templateEvaulator.getTemplate() != null || generatorTemplate.isCopy()) {
                    applications.forEach(application -> {
                        evaulationContext.setVariable("applications", applications);
                        evaulationContext.setVariable("application", application);
                        templateEvaulator.getFactoryExpressionResult(application, Collection.class).stream().forEach(element -> {
                            tasks.add(CompletableFuture.supplyAsync(() -> {
                                StandardEvaluationContext templateContext = clientGenerator.createSpringEvaulationContext();
                                templateContext.setVariable("applications", applications);
                                templateContext.setVariable("application", application);
                                templateContext.setVariable("template", generatorTemplate);
                                templateContext.setVariable("self", element);

                                Context.Builder contextBuilder = Context
                                        .newBuilder(element)
                                        .combine("applications", applications)
                                        .combine("application", application)
                                        .combine("template", generatorTemplate)
                                        .combine("self", element);

                                generatorTemplate.evalToContextBuilder(templateEvaulator, contextBuilder, templateContext);
                                GeneratedFile generatedFile = generateFile(clientGenerator, templateContext, templateEvaulator, generatorTemplate, contextBuilder, log);
                                sourcesByApplication.get(application).add(generatedFile);
                                return generatedFile;
                            }));

                        });
                    });
                }
            }
        });

        allFuture(tasks).get();

        return sourcesByApplication;
    }


    public static Collection<GeneratedFile> executeUi2ClientGeneration(ClientGenerator clientGenerator, Predicate<Application> applicationPredicate, Log log) throws Exception {

        Collection<GeneratedFile> sources = ConcurrentHashMap.newKeySet();

        Set<Application> applications = clientGenerator.getModelResourceSupport().getStreamOfUiApplication().filter(applicationPredicate).collect(Collectors.toSet());

        List<CompletableFuture<GeneratedFile>> tasks = new ArrayList<>();

        clientGenerator.getGeneratorTemplates().stream().forEach(generatorTemplate -> {
            StandardEvaluationContext evaulationContext = clientGenerator.createSpringEvaulationContext();
            if (generatorTemplate.getFactoryExpression() != null) {
                final TemplateEvaulator templateEvaulator;
                try {
                    templateEvaulator = generatorTemplate.getTemplateEvalulator(clientGenerator, evaulationContext);
                } catch (IOException e) {
                    throw new RuntimeException("Could not evaulate template", e);
                }

                if (templateEvaulator.getTemplate() != null || generatorTemplate.isCopy()) {

                    evaulationContext.setVariable("applications", applications);
                    evaulationContext.setVariable("template", generatorTemplate);

                    templateEvaulator.getFactoryExpressionResult(applications, Collection.class).stream().forEach(element -> {
                        tasks.add(CompletableFuture.supplyAsync(() -> {

                            StandardEvaluationContext templateContext = clientGenerator.createSpringEvaulationContext();
                            templateContext.setVariable("applications", applications);
                            templateContext.setVariable("template", generatorTemplate);
                            templateContext.setVariable("self", element);

                            Context.Builder contextBuilder = Context
                                    .newBuilder(element)
                                    .combine("applications", applications)
                                    .combine("template", generatorTemplate)
                                    .combine("self", element);

                            generatorTemplate.evalToContextBuilder(templateEvaulator, contextBuilder, evaulationContext);
                            GeneratedFile generatedFile = generateFile(clientGenerator, templateContext, templateEvaulator, generatorTemplate, contextBuilder, log);
                            sources.add(generatedFile);
                            return generatedFile;
                        }));
                    });
                }
            }
        });

        allFuture(tasks).get();

        return sources;
    }


    private static GeneratedFile generateFile(final ClientGenerator clientGenerator,
                                              final StandardEvaluationContext evaluationContext,
                                              final TemplateEvaulator templateEvaulator,
                                              final GeneratorTemplate generatorTemplate,
                                              final Context.Builder contextBuilder,
                                              final Log log) {

        GeneratedFile generatedFile = new GeneratedFile();
        generatedFile.setOverwrite(templateEvaulator.getOverWriteExpression().getValue(evaluationContext, Boolean.class));
        generatedFile.setPath(templateEvaulator.getPathExpression().getValue(evaluationContext, String.class));

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
            } catch (Exception e) {
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
        GeneratorIgnore generatorIgnore = new GeneratorIgnore(directory.toPath());
        return e -> {
            File output = new File(directory, outputNameFunction.apply(e.getKey()));
            e.getValue().stream().forEach(f -> {
                File outFile = new File(output, f.getPath());
                outFile.getParentFile().mkdirs();
                if ((!outFile.exists() || (f.getOverwrite())) && !generatorIgnore.shouldExcludeFile(outFile.toPath())) {
                    try {
                        ByteStreams.copy(new ByteArrayInputStream(f.getContent()), new FileOutputStream(outFile));
                    } catch (Exception exception) {
                        log.error("Could not write file: " + outFile.getAbsolutePath(), exception);
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
        return UriHelper.calculateRelativeURI(Ui2Client.class.getProtectionDomain().getCodeSource().getLocation().toURI(), TEMPLATE_ROOT_TATAMI_UI_2_CLIENT);
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateUi2ClientTemplateScriptURI(String template) {
        return UriHelper.calculateRelativeURI(Ui2Client.class.getProtectionDomain().getCodeSource().getLocation().toURI(), TEMPLATE_ROOT_TATAMI_UI_2_CLIENT + template);
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


    public static <T> CompletableFuture<List<T>> allFuture(List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }
    @Deprecated
    // TODO: Compatibiliyu reasons
    public static void main(String[] args) throws Exception {
        Ui2FlutterClient.main(args);
    }
}
