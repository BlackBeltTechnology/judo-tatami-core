package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.loadEsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Esm2PsmNorthwindTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_ESM_MODEL = "northwind-esm.model";
    public static final String NORTHWIND_PSM_MODEL = "esmNorthwind-psm.model";

    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    URIHandler uriHandler;
    Log slf4jlog;
    EsmModel esmModel;
    PsmModel psmModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = loadEsmModel(esmLoadArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_ESM_MODEL))
                .name(NORTHWIND));

        // TODO: Check it is valid or not as metamodel?
        assertTrue(esmModel.isValid());

        /*
        // TODO: Check semantic check is Okay?
        validateEsm(new Slf4jLog(log), esmModel,
                UriUtil.resolve(".", EsmModel.class.getClassLoader().getResource("validations/esm.evl").toURI()));
        */
        psmModel = PsmModel.buildPsmModel()
                .name(NORTHWIND)
                .build();
    }

    @Test
    public void testEsm2PsmTransformation() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        /*Esm2PsmTransformationTrace esm2PsmTransformationTrace = */
        executeEsm2PsmTransformation(
                esmModel,
                psmModel,
                new Slf4jLog(log),
                calculateEsm2PsmTransformationScriptURI());

        //assertTrue(psmModel.isValid());

        //assertDoesNotThrow(() -> {
        psmModel.savePsmModel(psmSaveArgumentsBuilder()
                .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND_PSM_MODEL))));
        //});
    }
}