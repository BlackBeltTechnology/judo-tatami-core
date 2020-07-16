package hu.blackbelt.judo.tatami.ui2flutter;

import static hu.blackbelt.judo.tatami.ui2flutter.Ui2Flutter.executeUi2FlutterGeneration;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static hu.blackbelt.judo.tatami.ui2flutter.Ui2Flutter.getGeneratedFilesAsZip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteStreams;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ui2FlutterTest {

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

        executeEsm2UiTransformation(esmModel, "desktop", 12, uiModel);
    }

    @Test
    public void testExecuteUi2FlutterGeneration() throws Exception {
        try (OutputStream zipOutputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, TEST + "-flutter.zip"))) {
            ByteStreams.copy(
                    getGeneratedFilesAsZip(executeUi2FlutterGeneration(uiModel)),
                    zipOutputStream
            );
        }
    }

}
