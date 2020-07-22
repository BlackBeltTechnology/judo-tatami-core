package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.io.*;
import com.google.common.base.Charsets;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
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
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.LoadArguments.uiLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.loadUi;

@Slf4j
public class Ui2Client {

    public static final String SCRIPT_ROOT_TATAMI_UI_2_FLUTTER = "tatami/ui2flutter/templates/";

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates) throws Exception {
        return executeUi2FlutterGeneration(uiModel, generatorTemplates, new Slf4jLog(log), calculateUi2FlutterTemplateScriptURI());
    }

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log) throws Exception {
        return executeUi2FlutterGeneration(uiModel, generatorTemplates, log, calculateUi2FlutterTemplateScriptURI());
    }

    public static Collection<GeneratedFile> executeUi2FlutterGeneration(UiModel uiModel, Collection<GeneratorTemplate> generatorTemplates, Log log,
                                                            URI scriptDir) throws Exception {

        TemplateLoader scriptDirectoryTemplateLoader = new ClientGeneratorTemplateLoader(scriptDir);
        scriptDirectoryTemplateLoader.setSuffix(".hbs");

        Handlebars handlebars = new Handlebars();
        handlebars.with(scriptDirectoryTemplateLoader);
        handlebars.setStringParams(true);
        handlebars.setCharset(Charsets.UTF_8);
        UiModelResourceSupport modelResourceSupport = loadUi(uiLoadArgumentsBuilder()
                .uri(org.eclipse.emf.common.util.URI.createURI("ui:" + uiModel.getName()))
                .resourceSet(uiModel.getResourceSet()).build());

        Collection<GeneratedFile> sourceFiles = new HashSet<>();
        for (GeneratorTemplate generatorTemplate : generatorTemplates) {
            ExpressionParser parser = new SpelExpressionParser();

            if (generatorTemplate.getFactoryExpression() != null) {
                final Expression factoryExpression = parser.parseExpression(generatorTemplate.getFactoryExpression());
                final Expression pathExpression = parser.parseExpression(generatorTemplate.getPathExpression());
                final Expression overWriteExpression = parser.parseExpression(generatorTemplate.getOverwriteExpression());
                final Template template;
                if (generatorTemplate.getTemplate() != null && !"".equals(generatorTemplate.getTemplate().trim())) {
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


                if (template != null) {
                    modelResourceSupport.getStreamOfUiApplication().forEach(app -> {
                        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
                        evaluationContext.setVariable("model", app);
                        evaluationContext.setVariable("template", generatorTemplate);

                        Collection<EObject> processingElements = factoryExpression.getValue(evaluationContext, app, Collection.class);

                        processingElements.stream().forEach(element -> {
                            evaluationContext.setVariable("element", element);
                            String path = pathExpression.getValue(evaluationContext, String.class);
                            Boolean overwite = overWriteExpression.getValue(evaluationContext, Boolean.class);

                            Context.Builder contextBuilder = Context
                                    .newBuilder(element)
                                    .combine("app", app)
                                    .combine("template", generatorTemplate);

                            generatorTemplate.getTemplateContext().stream().forEach(ctx -> {
                                contextBuilder.combine(ctx.getName(),
                                        templateExpressions.get(ctx.getName()).getValue(evaluationContext, ctx.getClazz()));
                            });
                            StringWriter sourceFile = new StringWriter();
                            try {
                                template.apply(contextBuilder.build(), sourceFile);
                            } catch (IOException e) {
                                log.error("Could not generate template: " + path);
                            }
                            GeneratedFile generatedFile = new GeneratedFile();
                            generatedFile.setOverwrite(overwite);
                            generatedFile.setPath(path);
                            generatedFile.setSource(sourceFile.toString());
                            sourceFiles.add(generatedFile);
                        });

                    });
                }
            }
        }
        return sourceFiles;
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
