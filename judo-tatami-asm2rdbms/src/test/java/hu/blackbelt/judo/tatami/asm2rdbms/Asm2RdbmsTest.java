package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;

@Slf4j
public class Asm2RdbmsTest {

    public static final String NORTHWIND_ASM_MODEL = "northwind-asm.model";
    public static final String NORTHWIND_RDBMS_MODEL = "northwind-rdbms.model";
    public static final String NORTHWIND_ASM_2_RDBMS_MODEL = "northwind-asm2rdbms.model";
    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String MODEL = "model";

    public static final URI NORTHWIND_ASM_2_RDBMS_TRACE_URI =
            URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_2_RDBMS_MODEL).getAbsolutePath());

    Log slf4jlog;
    AsmModel asmModel;
    RdbmsModel rdbmsModel;

    @Before
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        asmModel = loadAsmModel(asmLoadArgumentsBuilder()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_MODEL).getAbsolutePath()))
                .name(NORTHWIND));

        // Create empty RDBMS model
        rdbmsModel = RdbmsModel.buildRdbmsModel()
                .uri(URI.createFileURI(new File(TARGET_TEST_CLASSES, NORTHWIND_RDBMS_MODEL).getAbsolutePath()))
                .name(NORTHWIND)
                .build();

        // The RDBMS model resourceset have to know the mapping models
        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testAsm2RdbmsTransformation() throws Exception {

        Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace =
                executeAsm2RdbmsTransformation(asmModel, rdbmsModel, new Slf4jLog(log),
                        new File(TARGET_TEST_CLASSES, "epsilon/transformations").toURI(),
                        new File(MODEL).toURI(),
                        "hsqldb");

        // Saving trace map
        ResourceSet traceResourceSetSaved = createAsm2RdbmsTraceResourceSet();
        Resource traceResoureSaved = traceResourceSetSaved.createResource(NORTHWIND_ASM_2_RDBMS_TRACE_URI);
        traceResoureSaved.getContents().addAll(getAsm2RdbmsTrace(asm2RdbmsTransformationTrace.getTrace()));
        traceResoureSaved.save(ImmutableMap.of());

        // Loading trace map
        ResourceSet traceLoadedResourceSet = createAsm2RdbmsTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(NORTHWIND_ASM_2_RDBMS_TRACE_URI);
        traceResoureLoaded.load(ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveAsm2RdbmsTrace(traceResoureLoaded, asmModel, rdbmsModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        rdbmsModel.saveRdbmsModel();
    }
}