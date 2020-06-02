package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseUtils;
import hu.blackbelt.judo.meta.rdbms.RdbmsJunctionTable;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
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
import static hu.blackbelt.judo.meta.rdbms.util.builder.RdbmsBuilders.*;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

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
            fail(format("ChangeSets are missing: %s", expected.toString()));
    }

    private void compareCreateTables(final Set<String> expected, final String changeSetId) {
        liquibaseUtils.getCreateTables(changeSetId)
                .orElseThrow(() -> new RuntimeException(changeSetId + " ChangeSet cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getTableName()), e.getTableName() + " CreateTable is not expected");
                    expected.remove(e.getTableName());
                });
        if (!expected.isEmpty())
            fail(format("Missing CreateTable from %s: %s", changeSetId, expected.toString()));
    }

    private void compareColumns(final Set<String> expected, final String changeSetId, final String tableName) {
        liquibaseUtils.getColumns(changeSetId, tableName)
                .orElseThrow(() -> new RuntimeException(tableName + " CreateTable cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getName()), e.getName() + " Column is not expected");
                    expected.remove(e.getName());
                });
        if (!expected.isEmpty())
            fail(format("Missing Column from %s: %s", tableName, expected.toString()));
    }

    private void compareAddPrimaryKeys(final Set<String> expected, final String changeSetId) {
        liquibaseUtils.getAddPrimaryKeys(changeSetId)
                .orElseThrow(() -> new RuntimeException(changeSetId + " ChangeSet cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getColumnNames()), e.getColumnNames() + " AddPrimaryKeys is not expected");
                    expected.remove(e.getColumnNames());
                });
        if (!expected.isEmpty())
            fail(format("Missing AddPrimaryKeys from %s: %s", changeSetId, expected.toString()));
    }

    private void compareAddForeignKeyConstraints(final Set<String> expected, final String changeSetId) {
        liquibaseUtils.getAddForeignKeyConstraints(changeSetId)
                .orElseThrow(() -> new RuntimeException(changeSetId + " ChangeSet cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getConstraintName()), e.getConstraintName() + " AddForeignKeyConstraints is not expected");
                    expected.remove(e.getConstraintName());
                });
        if (!expected.isEmpty())
            fail(format("Missing AddForeignKeyConstraints from %s: %s", changeSetId, expected.toString()));
    }

    private void compareAddNotNullConstraints(final Set<String> expected, final String changeSetId) {
        liquibaseUtils.getAddNotNullConstraints(changeSetId)
                .orElseThrow(() -> new RuntimeException(changeSetId + " ChangeSet cannot be found"))
                .forEach(e -> {
                    assertTrue(expected.contains(e.getColumnName()), e.getColumnName() + " AddNotNullConstraints is not expected");
                    expected.remove(e.getColumnName());
                });
        if (!expected.isEmpty())
            fail(format("Missing AddNotNullConstraints from %s: %s", changeSetId, expected.toString()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testContents() {
        /////////////////////
        // setup rdbms model

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
                                .withStorageByte(1)
                                .withScale(2)
                                .withPrecision(3)
                                .withSize(4)
                                .withMandatory(true)
                                .build()
                )
                .build();
        rdbmsTable1.getPrimaryKey().setSqlName(rdbmsTable1.getPrimaryKey().getName());
        rdbmsTable1.getPrimaryKey().setRdbmsTypeName("ID");

        // table 2
        final RdbmsTable rdbmsTable2 = newRdbmsTableBuilderInit("TestTable2")
                .withSqlName("TestTable2")
                .build();
        rdbmsTable2.getPrimaryKey().setSqlName(rdbmsTable2.getPrimaryKey().getName());
        rdbmsTable2.getPrimaryKey().setRdbmsTypeName("ID");

        // junction table
        final RdbmsJunctionTable rdbmsJunctionTable =
                newRdbmsJunctionTableBuilderInit("TestJunctTable", rdbmsTable1, rdbmsTable2).build();

        rdbmsJunctionTable.setSqlName(rdbmsJunctionTable.getName());
        rdbmsJunctionTable.getPrimaryKey().setSqlName(rdbmsJunctionTable.getPrimaryKey().getName());
        rdbmsJunctionTable.getPrimaryKey().setRdbmsTypeName("ID");
        rdbmsJunctionTable.getField1().setSqlName(rdbmsJunctionTable.getField1().getName());
        rdbmsJunctionTable.getField1().setRdbmsTypeName("FOREIGN_KEY");
        rdbmsJunctionTable.getField2().setSqlName(rdbmsJunctionTable.getField2().getName());
        rdbmsJunctionTable.getField2().setRdbmsTypeName("FOREIGN_KEY");

        rdbmsModel.addContent(
                newRdbmsModelBuilder()
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


        // setup rdbms model
        ///////////////////////////////////////
        // build liquibase model and transform

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
                    .file(new File("target/test-classes", format("testContents-%s-liquibase.xml", liquibaseModel.getName())))
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

        // build liquibase model and transform
        //////////////////////////////////////
        // initialize expected sets

        final String CHANGE_SET_CREATE_TABLE_ID = "create-table-";
        final String CHANGE_SET_CREATE_FOREIGN_KEY_ID = "create-foreignkeys-";
        final String CHANGE_SET_NOT_NULL_ID = "add-not-null-";

        Set<String> expectedChangeSets = new HashSet<>();
        Set<String> expectedCreateTable1 = new HashSet<>(); // rdbmsTable1
        Set<String> expectedCreateTable2 = new HashSet<>(); // rdbmsTable2
        Set<String> expectedCreateTable3 = new HashSet<>(); // junctiontable
        Set<String> expectedColumns1 = new HashSet<>();
        Set<String> expectedColumns2 = new HashSet<>();
        Set<String> expectedColumns3 = new HashSet<>();
        Set<String> expectedAddPrimaryKey1 = new HashSet<>();
        Set<String> expectedAddPrimaryKey2 = new HashSet<>();
        Set<String> expectedAddPrimaryKey3 = new HashSet<>();
        Set<String> expectedAddForeignKeyConstraint = new HashSet<>();
        Set<String> expectedAddNotNullConstraint = new HashSet<>();

        // initialize expected sets
        ///////////////////////////
        // fill expected sets

        expectedChangeSets.add(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName());
        expectedChangeSets.add(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName());
        expectedChangeSets.add(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName());
        expectedChangeSets.add(CHANGE_SET_CREATE_FOREIGN_KEY_ID + rdbmsJunctionTable.getSqlName());
        expectedChangeSets.add(CHANGE_SET_NOT_NULL_ID + rdbmsTable1.getSqlName());

        expectedCreateTable1.add(rdbmsTable1.getSqlName());
        expectedCreateTable2.add(rdbmsTable2.getSqlName());
        expectedCreateTable3.add(rdbmsJunctionTable.getSqlName());

        expectedColumns1.add("_id");
        expectedColumns1.add("NumberField");
        expectedColumns2.add("_id");
        expectedColumns3.add("_id");
        expectedColumns3.add("TestTable1_fk1");
        expectedColumns3.add("TestTable2_fk2");

        expectedAddPrimaryKey1.add("_id");
        expectedAddPrimaryKey2.add("_id");
        expectedAddPrimaryKey3.add("_id");

        expectedAddForeignKeyConstraint.add("TestTable1");
        expectedAddForeignKeyConstraint.add("TestTable2");

        expectedAddNotNullConstraint.add("NumberField");

        // fill expected sets
        //////////////////////////////////
        // run assertion on expected sets

        compareChangeSet(expectedChangeSets);

        compareCreateTables(expectedCreateTable1, CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName());
        compareCreateTables(expectedCreateTable2, CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName());
        compareCreateTables(expectedCreateTable3,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName());

        compareColumns(expectedColumns1,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(),
                rdbmsTable1.getSqlName());
        compareColumns(expectedColumns2,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName(),
                rdbmsTable2.getSqlName());
        compareColumns(expectedColumns3,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                rdbmsJunctionTable.getSqlName());

        compareAddPrimaryKeys(expectedAddPrimaryKey1,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName());
        compareAddPrimaryKeys(expectedAddPrimaryKey2,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName());
        compareAddPrimaryKeys(expectedAddPrimaryKey3,
                CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName());

        compareAddForeignKeyConstraints(expectedAddForeignKeyConstraint,
                CHANGE_SET_CREATE_FOREIGN_KEY_ID + rdbmsJunctionTable.getSqlName());

        compareAddNotNullConstraints(expectedAddNotNullConstraint,
                CHANGE_SET_NOT_NULL_ID + rdbmsTable1.getSqlName());

        // run assertion on expected sets
        /////////////////////////////////
        // check transform elements

        /////////////// TestTable1 ///////////////////
        assertEquals(
                "TestTable1",
                liquibaseUtils.getCreateTable(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(),
                        rdbmsTable1.getSqlName()).get().getRemarks()
        );
        assertEquals(
                "ID",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(),
                        rdbmsTable1.getSqlName(),
                        "_id").get().getType()
        );
        assertEquals(
                "TestTable1#_id",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(),
                        rdbmsTable1.getSqlName(),
                        "_id").get().getRemarks()
        );
        assertEquals(
                "DECIMAL(3, 2)",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(),
                        rdbmsTable1.getSqlName(),
                        "NumberField").get().getType()
        );
        assertEquals(
                "TestTable1#NumberField",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable1.getSqlName(),
                        rdbmsTable1.getSqlName(),
                        "NumberField").get().getRemarks()
        );
        assertEquals(
                "DECIMAL(3, 2)",
                liquibaseUtils.getAddNotNullConstraint(CHANGE_SET_NOT_NULL_ID + rdbmsTable1.getSqlName(),
                        "NumberField").get().getColumnDataType()
        );

        /////////////// TestTable2 ///////////////////
        assertEquals(
                "ID",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName(),
                        rdbmsTable2.getSqlName(),
                        "_id").get().getType()
        );
        assertEquals(
                "TestTable2#_id",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsTable2.getSqlName(),
                        rdbmsTable2.getSqlName(),
                        "_id").get().getRemarks()
        );

        //////////// RdbmsJunctionTable //////////////
        assertEquals(
                "ID",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                        rdbmsJunctionTable.getSqlName(),
                        "_id").get().getType()
        );
        assertEquals(
                "TestJunctTable#_id",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                        rdbmsJunctionTable.getSqlName(),
                        "_id").get().getRemarks()
        );
        assertEquals(
                "FOREIGN_KEY",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                        rdbmsJunctionTable.getSqlName(),
                        "TestTable1_fk1").get().getType()
        );
        assertEquals(
                "TestJunctTable#TestTable1_fk1",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                        rdbmsJunctionTable.getSqlName(),
                        "TestTable1_fk1").get().getRemarks()
        );
        assertEquals(
                "FOREIGN_KEY",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                        rdbmsJunctionTable.getSqlName(),
                        "TestTable2_fk2").get().getType()
        );
        assertEquals(
                "TestJunctTable#TestTable2_fk2",
                liquibaseUtils.getColumn(CHANGE_SET_CREATE_TABLE_ID + rdbmsJunctionTable.getSqlName(),
                        rdbmsJunctionTable.getSqlName(),
                        "TestTable2_fk2").get().getRemarks()
        );

    }

}
