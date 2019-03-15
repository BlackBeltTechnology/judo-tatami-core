package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.AsmModel;
import hu.blackbelt.judo.meta.psm.PsmModel;
import hu.blackbelt.judo.meta.psm.PsmModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;

import static hu.blackbelt.judo.meta.asm.AsmModelLoader.*;
import static hu.blackbelt.judo.meta.psm.PsmModelLoader.createPsmResourceSet;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;

@Slf4j
public class Psm2AsmTest {


    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                srcDir().getAbsolutePath());

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet psmResourceSet = createPsmResourceSet(uriHandler);
        psmModel = PsmModelLoader.loadPsmModel(
                psmResourceSet,
                URI.createURI("urn:northwind-judopsm.model"),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testPsm2AsmTransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler);

        // Create wirtual URN
        URI asmUri = URI.createURI("urn:" + psmModel.getName() + ".asm");
        Resource  asmResource = asmResourceSet.createResource(asmUri);

        AsmModel asmModel = AsmModel.asmModelBuilder()
                .name(psmModel.getName())
                .resource(asmResource)
                .uri(asmUri)
                .version(psmModel.getVersion())
                .build();

        executePsm2AsmTransformation(asmResourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(srcDir().getAbsolutePath(), "transformations/asm"));


        TreeIterator<Notifier> iter = asmResourceSet.getAllContents();
        while (iter.hasNext()) {
            final Notifier obj = iter.next();
            log.info(obj.toString());
        }
    }


    public File srcDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}