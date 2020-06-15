package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.calculateAsm2JaxrsapiTemplateScriptURI;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.executeAsm2JAXRSAPIGeneration;

@Slf4j
public class Asm2JAXRSAPITest {

    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    
    Log slf4jlog;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        NorthwindModelLoader northwindModelLoader = NorthwindModelLoader.createNorthwindModelLoader(NORTHWIND);
        asmModel = northwindModelLoader.getAsmModel();
    }


    @Test
    public void testExecuteAsm2JAXRSAPIGeneration() throws Exception {
        try (OutputStream bundleOutputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-rest.jar"))) {
            ByteStreams.copy(
                    executeAsm2JAXRSAPIGeneration(asmModel, new Slf4jLog(log),
                            calculateAsm2JaxrsapiTemplateScriptURI(),
                            new File(TARGET_TEST_CLASSES, "generated/java")),
                    bundleOutputStream
            );
        }
    }
}
