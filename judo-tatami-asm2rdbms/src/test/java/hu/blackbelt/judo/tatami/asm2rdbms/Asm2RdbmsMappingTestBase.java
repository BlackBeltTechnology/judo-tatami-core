package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.support.RdbmsModelResourceSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.buildRdbmsModel;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;

@Slf4j
public class Asm2RdbmsMappingTestBase {
    protected static final String ASM_MODEL_NAME = "TestAsmModel";
    protected static final String RDBMS_MODEL_NAME = "TestRdbmsModel";

    protected static final String TARGET_TEST_CLASSES = "target/test-classes";

    protected Slf4jLog logger;

    protected AsmModel asmModel;
    protected RdbmsModel rdbmsModel;

    protected RdbmsModelResourceSupport rdbmsModelResourceSupport;

    @BeforeEach
    protected void setUp() {
        logger = new Slf4jLog(log);

        asmModel = buildAsmModel().name(ASM_MODEL_NAME).build();
        rdbmsModel = buildRdbmsModel().name(RDBMS_MODEL_NAME).build();

        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());
    }

    protected void executeTransformation(String testName) throws Exception {
        Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace = executeAsm2RdbmsTransformation(asmModel, rdbmsModel, new Slf4jLog(log),
                calculateAsm2RdbmsTransformationScriptURI(),
                calculateAsm2RdbmsModelURI(), "hsqldb");
        logger.info("Execute asm2rdbms transformation");

        asmModel = asm2RdbmsTransformationTrace.getAsmModel();
        logger.info("Extract asm model from transformation trace");
        rdbmsModel = asm2RdbmsTransformationTrace.getRdbmsModel();
        logger.info("Extract rdbms model from transformation trace");

        rdbmsModelResourceSupport = RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder()
                .resourceSet(rdbmsModel.getResourceSet())
                .uri(rdbmsModel.getUri())
                .build();
        logger.info("Create rdbms model support from transformed rdbms model");

        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, "testTypeMapping" + "-" + testName + "-" + ASM_MODEL_NAME + ".model")));
        logger.info("Save asm model");
        rdbmsModel.saveRdbmsModel(rdbmsSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, "testTypeMapping" + "-" + testName + "-" + RDBMS_MODEL_NAME + ".model")));
        logger.info("Save transformed rdbms model");
        asm2RdbmsTransformationTrace.save(new File(TARGET_TEST_CLASSES, "testTypeMapping" + "-" + testName + "-" + "Asm2RdbmsTransformationTrace.model"));
    }

}
