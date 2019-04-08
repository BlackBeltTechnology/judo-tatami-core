package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.LocalAsmPackageRegistration;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.createRdbmsResourceSet;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.saveRdbmssModel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;

@Slf4j
public class Asm2RdbmsTest {

    public static final String ASM_2_RDBMS_MODEL = "asm2rdbms.model";
    public static final String TRACE_JQLEXTRACT_2_EXPRESSION = "trace:asm2rdbms";
    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String RDBMS_NORTHWIND = "rdbms:northwind";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String URN_NORTHWIND_RDBMS = "urn:northwind-rdbms.model";
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
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM),
                        URI.createURI(RDBMS_NORTHWIND), URI.createURI(URN_NORTHWIND_RDBMS))
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
    public void testAsm2RdbmsTransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet rdbmsResourceSet = createRdbmsResourceSet(uriHandler);

        RdbmsModel rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(asmModel.getName())
                .resourceSet(rdbmsResourceSet)
                .uri(URI.createURI(RDBMS_NORTHWIND))
                .version(asmModel.getVersion())
                .build();

        Asm2RdbmsTrackInfo asm2RdbmsTrackInfo = executeAsm2RdbmsTransformation(rdbmsResourceSet, asmModel, rdbmsModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations"),
                new File(targetDir(), "../../model"),
                "hsqldb");

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getAsm2RdbmsTrace(asm2RdbmsTrackInfo.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(targetDir().getAbsolutePath(), ASM_2_RDBMS_MODEL)), ImmutableMap.of());

        // Loading trace map
        ResourceSet traceLoadedResourceSet = createAsm2RdbmsTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_JQLEXTRACT_2_EXPRESSION));
        traceResoureLoaded.load(new FileInputStream(new File(targetDir().getAbsolutePath(), ASM_2_RDBMS_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveAsm2RdbmsTrace(traceResoureLoaded, asmModel, rdbmsModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.info(e.toString() + " -> " + t.toString());
            }
        }

        saveRdbmssModel(rdbmsModel);
    }


    public File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}