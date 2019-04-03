package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader;
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

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.*;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.createPsmResourceSet;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.*;

@Slf4j
public class Psm2AsmTest {


    public static final String PSM_2_ASM_MODEL = "psm2asm.model";
    public static final String TRACE_PSM_2_ASM = "trace:psm2asm";
    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String PSM_NORTHWIND = "psm:northwind";
    public static final String URN_NORTHWIND_JUDOPSM_MODEL = "urn:northwind-judopsm.model";
    public static final String URN_NORTHWIND_ASM = "urn:northwind.asm";
    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;

    @Before
    public void setUp() throws Exception {

        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), targetDir().getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(PSM_NORTHWIND), URI.createURI(URN_NORTHWIND_JUDOPSM_MODEL),
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM))
        );

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet psmResourceSet = createPsmResourceSet(uriHandler);
        psmModel = PsmModelLoader.loadPsmModel(
                psmResourceSet,
                URI.createURI(PSM_NORTHWIND),
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

        // Creating AsmModel definition
        AsmModel asmModel = AsmModel.asmModelBuilder()
                .name(psmModel.getName())
                .resourceSet(asmResourceSet)
                .uri(URI.createURI(ASM_NORTHWIND))
                .version(psmModel.getVersion())
                .build();


        // Make transformation which returns the tracr with the serialized URI's
        Psm2AsmTrackInfo psm2AsmTrackInfo = executePsm2AsmTransformation(asmResourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations/asm"));

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getPsm2AsmTrace(psm2AsmTrackInfo.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(targetDir().getAbsolutePath(), PSM_2_ASM_MODEL)), ImmutableMap.of());

        // Loadeing trace map
        ResourceSet traceLoadedResourceSet = createPsm2AsmTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_PSM_2_ASM));
        traceResoureLoaded.load(new FileInputStream(new File(targetDir().getAbsolutePath(), PSM_2_ASM_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolvePsm2AsmTrace(traceResoureLoaded, psmModel, asmModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.info(e.toString() + " -> " + t.toString());
            }
        }

        saveAsmModel(asmModel);
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