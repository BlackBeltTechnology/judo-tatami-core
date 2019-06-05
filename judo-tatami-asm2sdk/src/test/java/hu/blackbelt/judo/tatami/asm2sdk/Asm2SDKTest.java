package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.LocalAsmPackageRegistration;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;

@Slf4j
public class Asm2SDKTest {

    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String NORTHWIND = "northwind";
    public static final String VERSION = "1.0.0";
    
    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), targetDir().getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM))
        );
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler, new LocalAsmPackageRegistration());
        asmModel = AsmModelLoader.loadAsmModel(
                asmResourceSet,
                URI.createURI(ASM_NORTHWIND),
                NORTHWIND,
                VERSION);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testExecuteAsm2JAXRSAPIGeneration() throws Exception {
        try (OutputStream outputStream =
                     new FileOutputStream(new File(targetDir().getAbsolutePath(), NORTHWIND + "-sdk.jar"))) {
            ByteStreams.copy(
                    Asm2SDK.executeAsm2SDKGeneration(new ResourceSetImpl(), asmModel, new Slf4jLog(log),
                            new File(srcDir().getAbsolutePath(), "epsilon/templates"),
                            new File(targetDir().getAbsolutePath(), "generated/java")),
                    outputStream
            );
        }
    }


    public File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

    public File srcDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File srcDir = new File(relPath, "../../src/main");
        return srcDir;
    }

}