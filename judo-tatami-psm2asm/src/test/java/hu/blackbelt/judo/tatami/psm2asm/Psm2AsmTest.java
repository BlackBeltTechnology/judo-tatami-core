package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.support.AsmModelResourceSupport;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
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
import java.util.Optional;

import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.*;

@Slf4j
public class Psm2AsmTest {


    public static final String PSM_2_ASM_MODEL = "psm2asm.model";
    public static final String TRACE_PSM_2_ASM = "trace:psm2asm";
    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String PSM_NORTHWIND = "psm:northwind";
    public static final String URN_NORTHWIND_PSM_MODEL = "urn:northwind-psm.model";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND = "northwind";

    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {

        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), TARGET_TEST_CLASSES)),
                ImmutableMap.of(
                        URI.createURI(PSM_NORTHWIND), URI.createURI(URN_NORTHWIND_PSM_MODEL),
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM))
        );

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = PsmModel.loadPsmModel(PsmModel.LoadArguments.loadArgumentsBuilder()
                .uri(URI.createURI(PSM_NORTHWIND))
                .uriHandler(Optional.of(uriHandler))
                .name(NORTHWIND)
                .build());


        // Create empty ASM model
        AsmModelResourceSupport asmModelResourceSupport = AsmModelResourceSupport.asmModelResourceSupportBuilder()
                .uriHandler(Optional.of(uriHandler))
                .build();

        asmModel = AsmModel.buildAsmModel()
                .asmModelResourceSupport(asmModelResourceSupport)
                .name(NORTHWIND)
                .uri(URI.createURI(PSM_NORTHWIND))
                .build();

    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testPsm2AsmTransformation() throws Exception {


        // Make transformation which returns the trace with the serialized URI's
        Psm2AsmTransformationTrace psm2AsmTransformationTrace = executePsm2AsmTransformation(
                asmModel.getResourceSet(),
                psmModel,
                asmModel,
                new Slf4jLog(log),
                new File("src/main/epsilon/transformations/asm").toURI());

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getPsm2AsmTrace(psm2AsmTransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(TARGET_TEST_CLASSES, PSM_2_ASM_MODEL)), ImmutableMap.of());

        // Loadeing trace map
        ResourceSet traceLoadedResourceSet = createPsm2AsmTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_PSM_2_ASM));
        traceResoureLoaded.load(new FileInputStream(new File(TARGET_TEST_CLASSES, PSM_2_ASM_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolvePsm2AsmTrace(traceResoureLoaded, psmModel, asmModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        asmModel.saveAsmModel();
    }


}