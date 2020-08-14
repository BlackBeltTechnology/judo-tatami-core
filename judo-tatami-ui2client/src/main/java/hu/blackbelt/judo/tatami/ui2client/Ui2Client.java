package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
import hu.blackbelt.judo.tatami.ui2client.flutter.FlutterHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.LoadArguments.uiLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.loadUi;

@Slf4j
public class Ui2Client {

    public static final String TEMPLATE_ROOT_TATAMI_UI_2_CLIENT = "templates/";

    public static Map<Application, Collection<GeneratedFile>> executeUi2ClientGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates) throws Exception {
        return executeUi2ClientGeneration(uiModel, generatorTemplates, new Slf4jLog(log), calculateUi2ClientTemplateScriptURI());
    }

    public static Map<Application, Collection<GeneratedFile>> executeUi2ClientGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log) throws Exception {
        return executeUi2ClientGeneration(uiModel, generatorTemplates, log, calculateUi2ClientTemplateScriptURI());
    }

    public static Map<Application, Collection<GeneratedFile>> executeUi2ClientGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log,
                                                                       URI scriptDir) throws Exception {

        ClientGeneratorTemplateLoader scriptDirectoryTemplateLoader = new ClientGeneratorTemplateLoader(scriptDir);
        //scriptDirectoryTemplateLoader.setSuffix(".hbs");
        scriptDirectoryTemplateLoader.setSuffix("");

        Handlebars handlebars = new Handlebars();
        handlebars.with(scriptDirectoryTemplateLoader);
        handlebars.setStringParams(true);
        handlebars.setCharset(Charsets.UTF_8);
        handlebars.registerHelpers(FlutterHelper.class);
        UiModelResourceSupport modelResourceSupport = loadUi(uiLoadArgumentsBuilder()
                .uri(org.eclipse.emf.common.util.URI.createURI("ui:" + uiModel.getName()))
                .resourceSet(uiModel.getResourceSet()).build());

        Map<Application, Collection<GeneratedFile>> sources = new HashMap<>();
        modelResourceSupport.getStreamOfUiApplication().forEach(app -> { sources.put(app, new HashSet<>()); });

        //Collection<GeneratedFile> sourceFiles = new HashSet<>();
        for (GeneratorTemplate generatorTemplate : generatorTemplates) {
            ExpressionParser parser = new SpelExpressionParser();

            if (generatorTemplate.getFactoryExpression() != null) {
                final Expression factoryExpression = parser.parseExpression(generatorTemplate.getFactoryExpression());
                final Expression pathExpression = parser.parseExpression(generatorTemplate.getPathExpression());
                final Expression overWriteExpression = parser.parseExpression(generatorTemplate.getOverwriteExpression());

                final Template template;
                if (generatorTemplate.isCopy()) {
                    template = null;
                } else if (generatorTemplate.getTemplate() != null && !"".equals(generatorTemplate.getTemplate().trim())) {
                    template = handlebars.compileInline(generatorTemplate.getTemplate());
                } else if (generatorTemplate.getTemplateName() != null && !"".equals(generatorTemplate.getTemplateName().trim())) {
                    template = handlebars.compile(generatorTemplate.getTemplateName());
                } else {
                    template = null;
                }

                Map<String, Expression> templateExpressions = new HashMap<>();
                generatorTemplate.getTemplateContext().stream().forEach(ctx -> {
                    final Expression contextTemplate = parser.parseExpression(ctx.getExpression());
                    templateExpressions.put(ctx.getName(), contextTemplate);
                });


                if (template != null || generatorTemplate.isCopy()) {
                    modelResourceSupport.getStreamOfUiApplication().forEach(app -> {
                        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
                        evaluationContext.setVariable("application", app);
                        evaluationContext.setVariable("template", generatorTemplate);
                        // TODO: Generalized way
                        FlutterHelper.registerSpEL(evaluationContext);

                        Collection<Object> processingElements = factoryExpression.getValue(evaluationContext, app, Collection.class);

                        processingElements.stream().forEach(element -> {
                            evaluationContext.setVariable("self", element);
                            String path = pathExpression.getValue(evaluationContext, String.class);
                            Boolean overwite = overWriteExpression.getValue(evaluationContext, Boolean.class);

                            Context.Builder contextBuilder = Context
                                    .newBuilder(element)
                                    .combine("application", app)
                                    .combine("template", generatorTemplate);

                            generatorTemplate.getTemplateContext().stream().forEach(ctx -> {
                                try {
                                    contextBuilder.combine(ctx.getName(),
                                            templateExpressions.get(ctx.getName()).getValue(evaluationContext,
                                                    Ui2Client.class.getClassLoader().loadClass(ctx.getClassName())));
//                                            templateExpressions.get(ctx.getName()).getValue(evaluationContext));

                                } catch (ClassNotFoundException e) {
                                    log.error("Class not found: " + ctx.getClassName());
                                }
                            });

                            GeneratedFile generatedFile = new GeneratedFile();
                            generatedFile.setOverwrite(overwite);
                            generatedFile.setPath(path);

                            if (generatorTemplate.isCopy()) {
                                String location = generatorTemplate.getTemplateName();
                                if (location.startsWith("/")) {
                                    location =  location.substring(1);
                                }
                                location = scriptDirectoryTemplateLoader.resolve(location);
                                try {
                                    URL resource = scriptDirectoryTemplateLoader.getResource(location);
                                    generatedFile.setContent(ByteStreams.toByteArray(resource.openStream()));
                                } catch (IOException e) {
                                }
                            } else {
                                StringWriter sourceFile = new StringWriter();
                                try {
                                    template.apply(contextBuilder.build(), sourceFile);
                                } catch (IOException e) {
                                    log.error("Could not generate template: " + path);
                                }
                                generatedFile.setContent(sourceFile.toString().getBytes(Charsets.UTF_8));
                            }
                            sources.get(app).add(generatedFile);
                        });

                    });
                }
            }
        }
        return sources;
    }

    public static Map<Application, InputStream> executeUi2ClientGenerationAsZip(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates) throws Exception {
        return executeUi2ClientGeneration(uiModel, generatorTemplates, new Slf4jLog(log), calculateUi2ClientTemplateScriptURI()).entrySet()
                .stream().collect(Collectors.toMap(e -> e.getKey(), e -> getGeneratedFilesAsZip(e.getValue())));
    }

    public static Map<Application, InputStream> executeUi2ClientGenerationAsZip(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log) throws Exception {
        return executeUi2ClientGeneration(uiModel, generatorTemplates, log, calculateUi2ClientTemplateScriptURI()).entrySet()
                .stream().collect(Collectors.toMap(e -> e.getKey(), e -> getGeneratedFilesAsZip(e.getValue())));

    }

    public static Map<Application, InputStream> executeUi2ClientGenerationAsZip(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log, URI scriptDir) throws Exception {
        return executeUi2ClientGeneration(uiModel, generatorTemplates, log, scriptDir).entrySet()
                .stream().collect(Collectors.toMap(e -> e.getKey(), e -> getGeneratedFilesAsZip(e.getValue())));
    }

    private static Consumer<Map.Entry<Application, Collection<GeneratedFile>>> getDirectoryWriter(File directory) {
        return e -> {
            File output = new File(directory, e.getKey().getName().replaceAll("[^\\.A-Za-z0-9_]", "_"));
            e.getValue().stream().forEach(f -> {
                File outFile = new File(output, f.getPath());
                outFile.getParentFile().mkdirs();
                if (!outFile.exists() || (f.getOverwrite())) {
                    try {
                        ByteStreams.copy(new ByteArrayInputStream(f.getContent()), new FileOutputStream(outFile));
                    } catch (IOException ioException) {
                        log.error("Could not write file: " + outFile.getAbsolutePath(), e);
                    }
                }
            });
        };
    }
    public static void executeUi2ClientGenerationToDirectory(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, File directory) throws Exception {
        executeUi2ClientGeneration(uiModel, generatorTemplates, new Slf4jLog(log), calculateUi2ClientTemplateScriptURI()).entrySet()
                .stream().forEach(getDirectoryWriter(directory));
    }

    public static void executeUi2ClientGenerationToDirectory(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, File directory, Log log) throws Exception {
        executeUi2ClientGeneration(uiModel, generatorTemplates, log, calculateUi2ClientTemplateScriptURI()).entrySet()
                .stream().forEach(getDirectoryWriter(directory));

    }

    public static void executeUi2ClientGenerationToDirectory(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, File directory, Log log, URI scriptDir) throws Exception {
        executeUi2ClientGeneration(uiModel, generatorTemplates, log, scriptDir).entrySet()
                .stream().forEach(getDirectoryWriter(directory));
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
}
