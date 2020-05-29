package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseUtils;
import hu.blackbelt.judo.meta.rdbms.RdbmsJunctionTable;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.util.builder.RdbmsModelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsUtils.newRdbmsJunctionTableBuilderInit;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsUtils.newRdbmsTableBuilderInit;
import static hu.blackbelt.judo.meta.rdbms.util.builder.RdbmsBuilders.newRdbmsConfigurationBuilder;
import static hu.blackbelt.judo.meta.rdbms.util.builder.RdbmsBuilders.newRdbmsValueFieldBuilder;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class Rdbms2LiquibaseContentTest {

    private static Slf4jLog logger = new Slf4jLog(log);

    private RdbmsModel rdbmsModel;
    private LiquibaseModel liquibaseModel;
    private LiquibaseUtils liquibaseUtils;

    private void compareChangeSet(final Set<String> expected) {
        liquibaseUtils.getChangeSets()
                .orElseThrow(() -> new RuntimeException("No ChangeSet found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getId()), e.getId() + " ChangeSet is not expected");
                    expected.remove(e.getId());
                });
        if (!expected.isEmpty())
            fail("ChangeSets are missing: " + expected.toString());
    }

    private void compareCreateTables(final Set<String> expected, final String changeSetId) {
        liquibaseUtils.getCreateTables(changeSetId)
                .orElseThrow(() -> new RuntimeException(changeSetId + " ChangeSet cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getTableName()), e.getTableName() + " CreateTable is not expected");
                    expected.remove(e.getTableName());
                });
        if (!expected.isEmpty())
            fail(String.format("Missing CreateTable from %s: %s", changeSetId, expected.toString()));
    }

    private void compareColumns(final Set<String> expected, final String changeSetId, final String tableName) {
        liquibaseUtils.getColumns(changeSetId, tableName)
                .orElseThrow(() -> new RuntimeException(tableName + " CreateTable cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getName()), e.getName() + " Column is not expected");
                    expected.remove(e.getName());
                });
        if (!expected.isEmpty())
            fail(String.format("Missing Column from %s: %s", tableName, expected.toString()));
    }

    @Test
    //@Disabled
    public void testContents() {
        final String MODEL_NAME = "TestModel";
        rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(MODEL_NAME)
                .build();

        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());

        // table 1
        final RdbmsTable rdbmsTable1 = newRdbmsTableBuilderInit("TestTable1")
                .withSqlName("TestTable1")
                .withFields(
                        newRdbmsValueFieldBuilder()
                                .withName("NumberField")
                                .withUuid("TestTable1#NumberField")
                                .withSqlName("NumberField")
                                .withRdbmsTypeName("DECIMAL")
                                .withStorageByte(10)
                                .withScale(10)
                                .withPrecision(10)
                                .withSize(10)
                                .withMandatory(true)
                                .build()
                )
                .build();
        rdbmsTable1.getPrimaryKey().setSqlName(rdbmsTable1.getPrimaryKey().getName());
        // table 2
        final RdbmsTable rdbmsTable2 = newRdbmsTableBuilderInit("TestTable2")
                .withSqlName("TestTable2")
                .build();
        rdbmsTable2.getPrimaryKey().setSqlName(rdbmsTable2.getPrimaryKey().getName());
        // junction table
        final RdbmsJunctionTable rdbmsJunctionTable =
                newRdbmsJunctionTableBuilderInit("TestJunctTable", rdbmsTable1, rdbmsTable2).build();

        rdbmsJunctionTable.setSqlName(rdbmsJunctionTable.getName());
        rdbmsJunctionTable.getPrimaryKey().setSqlName(rdbmsJunctionTable.getPrimaryKey().getName());
        rdbmsJunctionTable.getField1().setSqlName(rdbmsJunctionTable.getField1().getName());
        rdbmsJunctionTable.getField2().setSqlName(rdbmsJunctionTable.getField2().getName());

        rdbmsModel.addContent(
                RdbmsModelBuilder.create()
                        .withRdbmsTables(rdbmsTable1)
                        .withRdbmsTables(rdbmsTable2)
                        .withRdbmsTables(rdbmsJunctionTable)
                        .withConfiguration(
                                newRdbmsConfigurationBuilder()
                                        .withDialect("hsqldb")
                                        .build()
                        )
                        .build()
        );

        liquibaseModel = buildLiquibaseModel()
                .name(MODEL_NAME)
                .build();

        try {
            rdbmsModel.saveRdbmsModel(RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder()
                    .file(new File("target/test-classes", format("testContents-%s-rdbms.model", rdbmsModel.getName())))
                    .build());
            executeRdbms2LiquibaseTransformation(
                    rdbmsModel,
                    liquibaseModel,
                    new Slf4jLog(log),
                    calculateRdbms2LiquibaseTransformationScriptURI(),
                    "hsqldb");
            liquibaseModel.saveLiquibaseModel(LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder()
                    .file(new File("target/test-classes", format("testContents-%s-liquibase.model", liquibaseModel.getName())))
                    .build());
        } catch (URISyntaxException e) {
            fail("URISyntaxException", e);
        } catch (RdbmsModel.RdbmsValidationException e) {
            fail("Rdbms model is not valid", e);
        } catch (LiquibaseModel.LiquibaseValidationException e) {
            fail("Liquibase model is not valid", e);
        } catch (IOException e) {
            logger.warn("Unable to save model(s)");
        } catch (Exception e) {
            fail("Unknown exception", e);
        }

        liquibaseUtils = new LiquibaseUtils(liquibaseModel.getResourceSet());

        final String CHANGE_SET_CREATE_TABLE_ID = "create-table-";
        final String CHANGE_SET_CREATE_FOREIGN_KEY_ID = "create-foreignkeys-";
        final String CHANGE_SET_NOT_NULL_ID = "add-not-null-";

        Set<String> expectedChangeSets = new HashSet<>();
        expectedChangeSets.add(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName());
        expectedChangeSets.add(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName());
        expectedChangeSets.add(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName());
        expectedChangeSets.add(CHANGE_SET_CREATE_FOREIGN_KEY_ID + rdbmsJunctionTable.getSqlName());
        expectedChangeSets.add(CHANGE_SET_NOT_NULL_ID + rdbmsTable1.getSqlName());

        compareChangeSet(expectedChangeSets);

        Set<String> expectedCreateTable1 = new HashSet<>(); // rdbmsTable1
        expectedCreateTable1.add(rdbmsTable1.getName());
        Set<String> expectedCreateTable2 = new HashSet<>(); // rdbmsTable2
        expectedCreateTable2.add(rdbmsTable2.getName());
        Set<String> expectedCreateTable3 = new HashSet<>(); // junctiontable
        expectedCreateTable3.add(rdbmsJunctionTable.getName());

        compareCreateTables(expectedCreateTable1, CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName());
        compareCreateTables(expectedCreateTable2, CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName());
        compareCreateTables(expectedCreateTable3,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName());


        Set<String> expectedColumns1 = new HashSet<>();
        expectedColumns1.add("_id");
        expectedColumns1.add("NumberField");
        Set<String> expectedColumns2 = new HashSet<>();
        expectedColumns2.add("_id");
        Set<String> expectedColumns3 = new HashSet<>();
        expectedColumns3.add("_id");
        expectedColumns3.add("TestTable1_fk1");
        expectedColumns3.add("TestTable2_fk2");

        compareColumns(expectedColumns1,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(), rdbmsTable1.getName());
        compareColumns(expectedColumns2,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName(), rdbmsTable2.getName());
        compareColumns(expectedColumns3,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(), rdbmsJunctionTable.getName());

        Set<String> expectedAddPrimaryKey = new HashSet<>();
        Set<String> expectedAddForeignKeyConstraint = new HashSet<>();
        Set<String> expectedAddNotNullConstraint = new HashSet<>();
        Set<String> expected = new HashSet<>();


    }

}
