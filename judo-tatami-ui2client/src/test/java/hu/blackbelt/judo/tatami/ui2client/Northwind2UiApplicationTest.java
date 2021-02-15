package hu.blackbelt.judo.tatami.ui2client;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.NavigationController;
import hu.blackbelt.judo.meta.ui.data.NumericType;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
import hu.blackbelt.judo.tatami.esm2ui.Esm2UiTransformationTrace;
import hu.blackbelt.judo.tatami.ui2client.flutter.FlutterHelper;
import hu.blackbelt.model.northwind.esm.NorthwindEsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Northwind2UiApplicationTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "northwind";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    String testName;
    Map<EObject, List<EObject>> resolvedTrace;
    UiModel uiModel;
    Esm2UiTransformationTrace esm2UiTransformationTrace;

    @BeforeEach
    void setUp() throws Exception {
        slf4jlog = new Slf4jLog(log);
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();
        uiModel = buildUiModel().name(TEST).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2ui.model";
        esm2UiTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));
        uiModel.saveUiModel(uiSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-ui.model")));
        esmModel.saveEsmModel(esmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-esm.model")));
    }

    private void transformEsm2Ui() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel, "default", 12, uiModel, new Slf4jLog(log),
                calculateEsm2UiTransformationScriptURI());

        assertTrue(uiModel.isValid());
        validateUi(slf4jlog, uiModel, calculateUiValidationScriptURI());
    }

    
    @Test
    void testCreateFlutterApplication() throws Exception {
        testName = "Northwind";

        esmModel = NorthwindEsmModel.fullDemo();

//        Load UI model from file for manual xml edit.
//        uiModel = uiModel.loadResource(UiModel.LoadArguments.uiLoadArgumentsBuilder()
//                .uri(org.eclipse.emf.common.util.URI.createURI("ui:" + TEST))
//                .file(new File("src/test/resources/nortwind-ui-with-actions.model"))
//                .build());

        transformEsm2Ui();
        /*
        Map<Application, Collection<GeneratedFile>> generatedFiles = Ui2Client.executeUi2ClientGeneration(uiModel,
                GeneratorTemplate.loadYamlURL(Ui2Client.calculateUi2ClientTemplateScriptURI().resolve("flutter/flutter.yaml").toURL()));
        for (Application app : generatedFiles.keySet()) {
            try (OutputStream zipOutputStream =
                         new FileOutputStream(new File(TARGET_TEST_CLASSES, TEST + "-" +
                                 app.getName().replaceAll("[^\\.A-Za-z0-9_]", "_") +
                                 "-flutter.zip"))) {
                ByteStreams.copy(getGeneratedFilesAsZip(generatedFiles.get(app)), zipOutputStream);
            }
        } */

        Ui2Client.executeUi2ClientGenerationToDirectory(Ui2FlutterClient.getFlutterClientGenerator(uiModel),
                new File(TARGET_TEST_CLASSES), Ui2FlutterClient.OUTPUT_NAME_GENERATOR_FUNCTION);

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        final Optional<NavigationController> navigationController = allUi(NavigationController.class)
                .findAny();
        assertTrue(navigationController.isPresent());

        assertTrue(navigationController.get() == application.get().getNavigationController());
    }

        
    static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    <T> Stream<T> allUi() {
        return asStream((Iterator<T>) uiModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allUi(final Class<T> clazz) {
        return allUi().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }

}
