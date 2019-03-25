package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.*;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.createPsmResourceSet;
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
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler, new LocalAsmPackageRegistration());

        // Create wirtual URN
        URI asmUri = URI.createURI("urn:" + psmModel.getName() + ".asm");
        Resource  asmResource = asmResourceSet.createResource(asmUri);
        //asmResourceSet.getURIConverter().getURIMap().put(asmUri, URI.createURI(""));
        //asmResourceSet.getURIConverter().getURIMap().put(asmUri, URI.createURI(""));

        AsmModel asmModel = AsmModel.asmModelBuilder()
                .name(psmModel.getName())
                .resource(asmResource)
                .uri(asmUri)
                .version(psmModel.getVersion())
                .build();

        executePsm2AsmTransformation(asmResourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(srcDir().getAbsolutePath(), "epsilon/transformations/asm"));

        /*

        TreeIterator<Notifier> iter = asmResourceSet.getAllContents();
        while (iter.hasNext()) {
            final Notifier obj = iter.next();
            log.info(obj.toString());
        }


        XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI(srcDir().getAbsolutePath()+"/northwind-asm.model")) {
            @Override
            protected boolean useUUIDs() {
                return true;
            }
        };
        xmiResource.getContents().addAll(EcoreUtil.copyAll(asmResource.getContents()));
        for (EObject e : asmResource.getContents()) {
            log.info(e.toString());
        }
        */

        saveAsmModel(asmModel);

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