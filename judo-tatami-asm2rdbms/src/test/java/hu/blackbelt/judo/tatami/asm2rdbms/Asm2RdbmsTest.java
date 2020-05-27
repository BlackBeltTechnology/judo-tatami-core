package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace.fromModelsAndTrace;

@Slf4j
public class Asm2RdbmsTest {
    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_RDBMS_MODEL = "northwind-rdbms.model";
    public static final String NORTHWIND_ASM_2_RDBMS_MODEL = "northwind-asm2rdbms.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    AsmModel asmModel;
    RdbmsModel rdbmsModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        NorthwindModelLoader northwindModelLoader = NorthwindModelLoader.createNorthwindModelLoader(NORTHWIND);
        asmModel = northwindModelLoader.getAsmModel();

        // Create empty RDBMS model
        rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(NORTHWIND)
                .build();

        // The RDBMS model resourceset have to know the mapping models
        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());
    }

    @Test
    public void testAsm2RdbmsTransformation() throws Exception {

        Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace =
                executeAsm2RdbmsTransformation(asmModel, rdbmsModel, new Slf4jLog(log),
                        calculateAsm2RdbmsTransformationScriptURI(),
                        calculateAsm2RdbmsModelURI(),
                        "hsqldb");

        // Saving trace map
        asm2RdbmsTransformationTrace.save(new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_2_RDBMS_MODEL));

        // Loading trace map
        Asm2RdbmsTransformationTrace asm2RdbmsTransformationTraceLoaded =
                fromModelsAndTrace(NORTHWIND, asmModel, rdbmsModel, new File(TARGET_TEST_CLASSES, NORTHWIND_ASM_2_RDBMS_MODEL));


        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = asm2RdbmsTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        rdbmsModel.saveRdbmsModel(rdbmsSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, NORTHWIND_RDBMS_MODEL)));
    }
}