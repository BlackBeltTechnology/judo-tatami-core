package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.LocalAsmPackageRegistration;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Map;

import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.*;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.executeAsm2RdbmsTransformation;

@Slf4j
public class Asm2RdbmsTest {


    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                srcDir().getAbsolutePath());

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler, new LocalAsmPackageRegistration());
        asmModel = AsmModelLoader.loadAsmModel(
                asmResourceSet,
                URI.createURI("urn:northwind-asm.model"),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testAsm2RdbmsTransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet rdbmsResourceSet = createRdbmsResourceSet(uriHandler);

        // Create wirtual URN
        URI rdbmsUri = URI.createURI("urn:" + asmModel.getName() + ".rdbms");
        Resource  rdbmsResource = rdbmsResourceSet.createResource(rdbmsUri);

        RdbmsModel rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(asmModel.getName())
                .resourceSet(rdbmsResourceSet)
                .uri(rdbmsUri)
                .version(asmModel.getVersion())
                .build();

        executeAsm2RdbmsTransformation(rdbmsResourceSet, asmModel, rdbmsModel, new Slf4jLog(log),
                new File(srcDir().getAbsolutePath(), "epsilon/transformations"),
                new File(srcDir(), "../../model"),
                "hsqldb");


    /*
        TreeIterator<Notifier> iter = rdbmsResourceSet.getAllContents();
        while (iter.hasNext()) {
            final Notifier obj = iter.next();
            log.debug(obj.toString());
        } */


        XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI(srcDir().getAbsolutePath()+"/northwind-rdbms.model"));
        xmiResource.getContents().addAll(EcoreUtil.copyAll(rdbmsResource.getContents()));
        for (EObject e : rdbmsResource.getContents()) {
            log.debug(e.toString());
        }

        final Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
        saveOptions.put(XMIResource.OPTION_DECLARE_XML,Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION,Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION,Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI,Boolean.FALSE);
        saveOptions.put(XMIResource.OPTION_ENCODING,"UTF-8");

        xmiResource.save(saveOptions);
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