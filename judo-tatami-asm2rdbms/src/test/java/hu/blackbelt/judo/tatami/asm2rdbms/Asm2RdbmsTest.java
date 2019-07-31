package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.support.RdbmsModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Optional;

import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;

@Slf4j
public class Asm2RdbmsTest {

    public static final String TRACE_ASM_2_RDBMS = "trace:asm2rdbms";
    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String RDBMS_NORTHWIND = "rdbms:northwind";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String URN_NORTHWIND_RDBMS = "urn:northwind-rdbms.model";
    public static final String URN_ASM_2_RDBMS_MODEL = "urn:asm2rdbms.model";
    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String MODEL = "model";

    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;
    RdbmsModel rdbmsModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), TARGET_TEST_CLASSES)),
                ImmutableMap.of(
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM),
                        URI.createURI(RDBMS_NORTHWIND), URI.createURI(URN_NORTHWIND_RDBMS),
                        URI.createURI(TRACE_ASM_2_RDBMS), URI.createURI(URN_ASM_2_RDBMS_MODEL)
                )
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

        // Create empty RDBMS model
        RdbmsModelResourceSupport rdbmsModelResourceSupport = RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder()
                .uriHandler(Optional.of(uriHandler))
                .build();

        rdbmsModel = RdbmsModel.buildRdbmsModel()
                .rdbmsModelResourceSupport(rdbmsModelResourceSupport)
                .name(NORTHWIND)
                .uri(URI.createURI(RDBMS_NORTHWIND))
                .build();

        // The RDBMS model resourceset have to know the mapping models
        RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());

    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testAsm2RdbmsTransformation() throws Exception {

        Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace = executeAsm2RdbmsTransformation(rdbmsModel.getResourceSet(), asmModel, rdbmsModel, new Slf4jLog(log),
                new File(TARGET_TEST_CLASSES, "epsilon/transformations").toURI(),
                new File(MODEL).toURI(),
                "hsqldb");

        // Saving trace map
        ResourceSet traceResourceSetSaved = createAsm2RdbmsTraceResourceSet(uriHandler);
        Resource traceResoureSaved = traceResourceSetSaved.createResource(URI.createURI(TRACE_ASM_2_RDBMS));
        traceResoureSaved.getContents().addAll(getAsm2RdbmsTrace(asm2RdbmsTransformationTrace.getTrace()));
        traceResoureSaved.save(ImmutableMap.of());

        // Loading trace map
        Asm2RdbmsTransformationTrace resolvedTraceModel = loadAsm2RdbmsTrace(URI.createURI(TRACE_ASM_2_RDBMS), uriHandler, asmModel, rdbmsModel);

        // Printing trace
        for (EObject e : resolvedTraceModel.getTrace().keySet()) {
            for (EObject t : resolvedTraceModel.getTrace().get(e)) {
                log.info(e.toString() + " -> " + t.toString());
            }
        }

        rdbmsModel.saveRdbmsModel();
    }
}