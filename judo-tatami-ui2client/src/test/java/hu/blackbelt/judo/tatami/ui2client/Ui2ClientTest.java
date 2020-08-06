package hu.blackbelt.judo.tatami.ui2client;

import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.executeUi2ClientGeneration;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.getGeneratedFilesAsZip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteStreams;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ui2ClientTest {

    private final String TARGET_TEST_CLASSES = "target/test-classes";
    private final String TEST = "test";

    Log slf4jlog;
    UiModel uiModel;

    @BeforeEach
    public void setUp() throws Exception {
        EsmModel esmModel = EsmModel.buildEsmModel().name(TEST).build();
        esmModel.addContent(SimpleOrderModel.createSimpleOrderModel());
        // Create empty UI model
        uiModel = UiModel.buildUiModel().name(TEST).build();
        
        GeneratorTemplate generatorTemplate;

        executeEsm2UiTransformation(esmModel, "desktop", 12, uiModel);

        uiModel.saveUiModel(UiModel.SaveArguments.uiSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, TEST + "-ui.model")).build());

    }

    @Test
    public void testExecuteUi2FlutterGeneration() throws Exception {
        Map<Application, Collection<GeneratedFile>> generatedFiles = Ui2Client.executeUi2ClientGeneration(uiModel,
                GeneratorTemplate.loadYamlURL(Ui2Client.calculateUi2ClientTemplateScriptURI().resolve("flutter/flutter.yaml").toURL()));
        for (Application app : generatedFiles.keySet()) {
            try (OutputStream zipOutputStream =
                         new FileOutputStream(new File(TARGET_TEST_CLASSES, TEST + "-" + app.getName() + "-flutter.zip"))) {
                ByteStreams.copy(getGeneratedFilesAsZip(generatedFiles.get(app)), zipOutputStream);
            }

        }
    }

}
