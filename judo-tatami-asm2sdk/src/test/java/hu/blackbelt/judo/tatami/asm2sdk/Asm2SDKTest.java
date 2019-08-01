package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.util.Optional;

@Slf4j
public class Asm2SDKTest {

    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    
    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), TARGET_TEST_CLASSES)),
                ImmutableMap.of(
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM))
        );
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = AsmModel.loadAsmModel(AsmModel.LoadArguments.loadArgumentsBuilder()
                .uri(URI.createURI(ASM_NORTHWIND))
                .uriHandler(Optional.of(uriHandler))
                .name(NORTHWIND)
                .build());
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testExecuteAsm2JAXRSAPIGeneration() throws Exception {
        try (OutputStream outputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND + "-sdk.jar"))) {
            ByteStreams.copy(
                    Asm2SDK.executeAsm2SDKGeneration(new ResourceSetImpl(), asmModel, new Slf4jLog(log),
                            new File(TARGET_TEST_CLASSES, "epsilon/templates").toURI(),
                            new File(TARGET_TEST_CLASSES, "generated/java")),
                    outputStream
            );
        }
    }
}
