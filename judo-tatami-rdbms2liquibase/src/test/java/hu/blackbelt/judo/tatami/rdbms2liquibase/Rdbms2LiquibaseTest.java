package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.model.northwind.Demo;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.HsqlConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseNamespaceFixUriHandler.fixUriOutputStream;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.executeAsm2RdbmsTransformation;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;

@Slf4j
public class Rdbms2LiquibaseTest {

    public static final String NORTHWIND = "northwind";
    public static final String NORTHWIND_RDBMS_MODEL = "northwind-rdbms_hsqldb.model";
    public static final String NORTHWIND_LIQUIBASE_MODEL = "northwind.changelog.xml";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    RdbmsModel rdbmsModel;
    LiquibaseModel liquibaseModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        final PsmModel psmModel = new Demo().fullDemo();

        // Create empty ASM model
        AsmModel asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .build();

        executePsm2AsmTransformation(psmModel, asmModel);

        // Create empty RDBMS model
        rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(NORTHWIND)
                .build();

        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());

        executeAsm2RdbmsTransformation(asmModel, rdbmsModel, "hsqldb");

        // Create empty LIQUIBASE model
        liquibaseModel = buildLiquibaseModel()
                .name(NORTHWIND)
                .build();
    }

    @Test
    public void testRdbms2LiquibaseTransformation() throws Exception {

        executeRdbms2LiquibaseTransformation(rdbmsModel, liquibaseModel, "hsqldb");

        liquibaseModel.saveLiquibaseModel(liquibaseSaveArgumentsBuilder()
                                                  .outputStream(fixUriOutputStream(
                                                          new FileOutputStream(new File(TARGET_TEST_CLASSES, NORTHWIND_LIQUIBASE_MODEL)))));

        // Executing on HSQLDB
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");

        Database liquibaseDb = new HsqlDatabase();
        liquibaseDb.setConnection(new HsqlConnection(connection));
        Liquibase liquibase = new Liquibase(
                new File(TARGET_TEST_CLASSES, NORTHWIND_LIQUIBASE_MODEL).getAbsolutePath(),
                new FileSystemResourceAccessor(),
                liquibaseDb);

        liquibase.update("full,1.0.0");
    }
}
