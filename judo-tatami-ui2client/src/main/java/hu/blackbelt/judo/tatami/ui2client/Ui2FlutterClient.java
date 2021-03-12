package hu.blackbelt.judo.tatami.ui2client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
import hu.blackbelt.judo.tatami.ui2client.flutter.FlutterHelper;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.*;

public class Ui2FlutterClient {

    public static final String CLIENT_NAME = "clientName";
    public static final String NAME = "name";
    public static final String ACTOR_FQ_NAME = "actorFqName";

    public static final Log log = new Slf4jLog(LoggerFactory.getLogger(Ui2FlutterClient.class));
    public static final String FLUTTER_YAML = "flutter.yaml";

    public static Function<Application, String> OUTPUT_NAME_GENERATOR_FUNCTION = (Application a) -> FlutterHelper.dart(a.getName());



    public static ClientGenerator getFlutterClientGenerator(UiModel uiModel, List<URI> overridedScriptUris) throws IOException, UiModelResourceSupport.UiValidationException {
        List<URI> scriptUris = new ArrayList<>();
        scriptUris.add(calculateUi2ClientTemplateScriptURI());
        scriptUris.addAll(overridedScriptUris);

        List<GeneratorTemplate> generatorTemplates = new ArrayList<>();
        generatorTemplates.addAll(GeneratorTemplate.loadYamlURL(Ui2Client.calculateUi2ClientTemplateScriptURI("flutter/" + FLUTTER_YAML).toURL()));
        // Search for overrided flutter yaml files
        for (URI uri : overridedScriptUris) {
            Collection<GeneratorTemplate> overridedTemplates = GeneratorTemplate.loadYamlURL(UriHelper.calculateRelativeURI(uri, FLUTTER_YAML).toURL());
            Collection<GeneratorTemplate> replaceableTemplates = new HashSet<>();
            generatorTemplates.forEach(t -> {
                overridedTemplates.stream().filter(o -> o.getTemplateName().equals(t.getTemplateName())).forEach(f -> replaceableTemplates.add(f));
            });
            generatorTemplates.removeAll(replaceableTemplates);
            generatorTemplates.addAll(overridedTemplates);
        }
        ClientGenerator clientGenerator = new ClientGenerator(uiModel, scriptUris, generatorTemplates);
        return clientGenerator;
    }

    public static ClientGenerator getFlutterClientGenerator(UiModel uiModel) throws IOException, UiModelResourceSupport.UiValidationException {
        return getFlutterClientGenerator(uiModel, Collections.EMPTY_LIST);
    }

    public static void generateFlutterClient(File uiModelFile,
                                             String modelName,
                                             File targetDirectory,
                                             Predicate<Application> applicationPredicate,
                                             String projectSkeleton,
                                             String openapiYamlNameTemplate,
                                             File overridePath
    ) throws Exception {

        UiModel uiModel = UiModel.loadUiModel(
                UiModel.LoadArguments.uiLoadArgumentsBuilder().file(uiModelFile).name(modelName));

        List<URI> overridedUris = new ArrayList<>();
        if (overridePath !=null) {
            if (!overridePath.exists()) {
                throw new IllegalArgumentException("Overrided path does not exists: " + overridePath);
            }
            overridedUris.add(overridePath.toURI());
        }
        Map<Application, Collection<GeneratedFile>> generatedApps =
                executeUi2ClientGenerationByApplication(
                        getFlutterClientGenerator(uiModel, overridedUris),
                        applicationPredicate,
                        log);

        Map<String, Map<String, String>> clientNames = generatedApps.entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey().getFQName(),
                e -> ImmutableMap.of(
                        ACTOR_FQ_NAME, e.getKey().getActor().getFQName(),
                        NAME, e.getKey().getActor().getName(),
                        CLIENT_NAME, OUTPUT_NAME_GENERATOR_FUNCTION.apply(e.getKey()))));

        new ObjectMapper().writeValue(new File(targetDirectory, modelName + ".json"), clientNames);

        String finalProjectSkeleton = projectSkeleton;
        String finalOpenapiYamlNameTemplate = openapiYamlNameTemplate;
        generatedApps.entrySet().stream()
                .forEach(getDirectoryWriter(targetDirectory, OUTPUT_NAME_GENERATOR_FUNCTION, log).andThen(e -> {

                    String clientName = clientNames.get(e.getKey().getFQName()).get(CLIENT_NAME);
                    if (finalOpenapiYamlNameTemplate != null) {
                        String actorFqName = modelName + "-" + FlutterHelper.className(e.getKey().getActor().getName());
                        String actorName = e.getKey().getName();

                        String sourceFileName = finalOpenapiYamlNameTemplate + File.separator + actorFqName
                                + "-openapi.yaml";

                        Path sourceFile = new File(sourceFileName).toPath();

                        if (!Files.exists(sourceFile)) {
                            throw new RuntimeException("File does not exists: " + sourceFileName);
                        }

                        Path destinationFile = new File(targetDirectory,
                                (clientName + File.separator +
                                        "lib" + File.separator +
                                        FlutterHelper.path(actorName) + File.separator +
                                        "rest" + File.separator +
                                        FlutterHelper.path(actorName) + ".yaml"))
                                .toPath();

                        if (Files.exists(sourceFile) && !Files.isDirectory(sourceFile)) {
                            try {
                                Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e2) {
                                throw new RuntimeException(e2);
                            }
                        }
                    }

                    if (finalProjectSkeleton != null) {
                        File mergeDirectory = new File(finalProjectSkeleton);
                        if (!mergeDirectory.exists() || !mergeDirectory.isDirectory()) {
                            throw new RuntimeException(finalProjectSkeleton + " does not exists or not directory");
                        }

                        Path sourceDir = mergeDirectory.toPath();
                        Path destinationDir = new File(targetDirectory, clientName).toPath();

                        log.info("Merge " + sourceDir.toString() + " to " + destinationDir.toString());

                        // Traverse the file tree and copy each file/directory.
                        try {
                            Files.walk(sourceDir)
                                    .forEach(sourcePath -> {
                                        try {
                                            Path targetPath = destinationDir.resolve(sourceDir.relativize(sourcePath));
                                            if (!Files.exists(targetPath)) {
                                                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                                            }
                                        } catch (IOException e1) {
                                            throw new RuntimeException(e1);
                                        }
                                    });
                        } catch (IOException e2) {
                            throw new RuntimeException(e2);
                        }
                    }
                }));


    }


    public static void main(String[] args) throws Exception {

        File uiModelFile = new File(args[0]);
        String modelName = args[1];
        File targetDirectory = new File(args[2]);
        String overridePath = args[3];
        Predicate<Application> applicationPredicate = (a) -> true;

        if (args.length > 4 && !args[4].equals("*")) {
            List<String> actors = Arrays.stream(args[4].split(","))
                    .map(s -> s.trim())
                    .filter(s -> s != null && s.length() > 0)
                    .collect(Collectors.toList());

            applicationPredicate = (Application a) -> actors.contains(a.getActor().getName());
        }

        String projectSkeleton = null;
        if (args.length > 5) {
            projectSkeleton = args[5];
        }

        String openapiYamlNameTemplate = null;
        if (args.length > 6) {
            openapiYamlNameTemplate = args[6];
        }

        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        File overrideDirectory = null;
        if (overridePath != null) {
            overrideDirectory = new File(overridePath);
            if (!overrideDirectory.exists()) {
                overrideDirectory = null;
            }
        }

        generateFlutterClient(uiModelFile, modelName, targetDirectory, applicationPredicate, projectSkeleton, openapiYamlNameTemplate, overrideDirectory);

    }
}
