package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.ChangeSet;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseNamespaceFixUriHandler;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseUtils;
import hu.blackbelt.judo.meta.rdbms.RdbmsField;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsUtils;
import hu.blackbelt.judo.tatami.rdbms2liquibase.datasource.RdbmsDatasourceByClassExtension;
import hu.blackbelt.judo.tatami.rdbms2liquibase.datasource.RdbmsDatasourceFixture;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.EList;
import org.eclipse.epsilon.common.util.UriUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsIncremental.transformRdbmsIncrementalModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.buildRdbmsModel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseIncremental.executeRdbms2LiquibaseIncrementalTransformation;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.datasource.RdbmsDatasourceFixture.DIALECT_HSQLDB;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.datasource.RdbmsDatasourceFixture.DIALECT_POSTGRESQL;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(RdbmsDatasourceByClassExtension.class)
public class Excel2RdbmsTest {

    private static final String ORIGINAL_MODEL_NAME = "OriginalModel";
    private static final String TARGET_TEST_CLASSES = "target/test-classes";
    private static final String GENERATED_SQL_LOCATION = TARGET_TEST_CLASSES + "/sql";
    private static final String GENERATED_REVIEW_LOCATION = "src/test/resources/review";

    @Test
    public void executeExcel2RdbmsModel(RdbmsDatasourceFixture datasource) throws Exception {
        // change dialect with -Ddialect maven property (default: hsqldb)
        final String dialect = datasource.getDialect();

        RdbmsModel originalModel = buildRdbmsModel().name(ORIGINAL_MODEL_NAME).build();
        RdbmsModel newModel = buildRdbmsModel().name("NewModel").build();

        // Execution context
        ExecutionContext excelToRdbmsEtlContext = executionContextBuilder()
                .log(new Slf4jLog(log))
                .resourceSet(originalModel.getResourceSet())
                .modelContexts(ImmutableList.of(
                        excelModelContextBuilder()
                                .name("EXCEL")
                                .aliases(singletonList("XLS"))
                                .excel(getUri(this.getClass(), "RdbmsIncrementalTests.xlsx").toString())
                                .excelConfiguration(getUri(this.getClass(), "mapping.xml").toString())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("ORIGINAL_MODEL")
                                .aliases(singletonList("ORIGINAL"))
                                .resource(originalModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("NEW_MODEL")
                                .aliases(singletonList("NEW"))
                                .resource(newModel.getResource())
                                .build()))
                .build();

        excelToRdbmsEtlContext.load();

        URI testRoot = getUri(Excel2RdbmsTest.class, "/");

        excelToRdbmsEtlContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(UriUtil.resolve("createExcelModel.etl", testRoot))
                        .parameters(singletonList(programParameterBuilder().name("dialect").value(dialect).build()))
                        .build());

        excelToRdbmsEtlContext.commit();
        excelToRdbmsEtlContext.close();

        final RdbmsUtils rdbmsUtilsOriginal = new RdbmsUtils(originalModel.getResourceSet());
        final RdbmsUtils rdbmsUtilsNew = new RdbmsUtils(newModel.getResourceSet());

        rdbmsUtilsOriginal.getAllRdbmsField()
                .orElseThrow(() -> new RuntimeException("There are no fields in model: " + originalModel.getName()))
                .forEach(field -> replaceTypeNames(field, dialect));
        rdbmsUtilsNew.getAllRdbmsField()
                .orElseThrow(() -> new RuntimeException("There are no fields in model: " + newModel.getName()))
                .forEach(field -> replaceTypeNames(field, dialect));

        saveRdbms(originalModel, dialect);
        saveRdbms(newModel, dialect);

        // fill models
        /////////////////////////////////////////
        // rdbms2liquibase (original)
        LiquibaseModel originalLiquibaseModel = buildLiquibaseModel().name(ORIGINAL_MODEL_NAME).build();
        executeRdbms2LiquibaseTransformation(originalModel, originalLiquibaseModel, dialect);

        saveLiquibase(originalLiquibaseModel, dialect);

        // rdbms2liquibase (original)
        /////////////////////////////////////////
        // delta model

        RdbmsModel incrementalModel = buildRdbmsModel().name("IncrementalModel").build();
        transformRdbmsIncrementalModel(originalModel, newModel, incrementalModel, dialect, true);

        saveRdbms(incrementalModel, dialect);

        LiquibaseModel dbCheckupModel = buildLiquibaseModel().name("DbCheckup").build();
        LiquibaseModel dbBackupLiquibaseModel = buildLiquibaseModel().name("DbBackup").build();
        LiquibaseModel beforeIncrementalModel = buildLiquibaseModel().name("BeforeIncremental").build();
        LiquibaseModel updateDataBeforeIncrementalModel = buildLiquibaseModel().name("UpdateDataBeforeIncremental").build();
        LiquibaseModel incrementalLiquibaseModel = buildLiquibaseModel().name("IncrementalModel").build();
        LiquibaseModel updateDataAfterIncrementalModel = buildLiquibaseModel().name("UpdateDataAfterIncremental").build();
        LiquibaseModel afterIncrementalModel = buildLiquibaseModel().name("AfterIncremental").build();
        LiquibaseModel dbDropBackupLiquibaseModel = buildLiquibaseModel().name("DbDropBackup").build();

        Map<String, String> missingScripts = executeRdbms2LiquibaseIncrementalTransformation(
                incrementalModel,
                dbCheckupModel,
                dbBackupLiquibaseModel,
                beforeIncrementalModel,
                updateDataBeforeIncrementalModel,
                incrementalLiquibaseModel,
                updateDataAfterIncrementalModel,
                afterIncrementalModel,
                dbDropBackupLiquibaseModel,
                dialect,
                GENERATED_SQL_LOCATION,
                GENERATED_REVIEW_LOCATION);

        saveLiquibase(dbCheckupModel, dialect);
        saveLiquibase(dbBackupLiquibaseModel, dialect);
        saveLiquibase(beforeIncrementalModel, dialect);
        saveLiquibase(updateDataBeforeIncrementalModel, dialect);
        saveLiquibase(incrementalLiquibaseModel, dialect);
        saveLiquibase(updateDataAfterIncrementalModel, dialect);
        saveLiquibase(afterIncrementalModel, dialect);
        saveLiquibase(dbDropBackupLiquibaseModel, dialect);

        assertEquals(8, missingScripts.keySet().size());
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_2_table_1_11_type_after.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_9_value_5_size.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_3_value_1_create.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_2_table_1_to_value_field_after.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_2_table_1_12_to_value_field_before.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_2_table_1_10_mandatory.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_2_table_1_6_to_foreign_key_after.sql")));
        assertTrue(missingScripts.keySet().stream().anyMatch(k -> k.endsWith("hsqldb_table_2_table_1_5_to_foreign_key_before.sql")));

        LiquibaseUtils liquibaseUtils = new LiquibaseUtils(updateDataBeforeIncrementalModel.getResourceSet());
        Optional<EList<ChangeSet>> optionalChangeSets = liquibaseUtils.getChangeSets();
        assertTrue(optionalChangeSets.isPresent());
        assertTrue(optionalChangeSets.get().stream().noneMatch(cs -> cs.getSqlFile().stream().anyMatch(f -> f.getPath().endsWith("hsqldb_table_9_value_4_size.sql"))));
        assertTrue(optionalChangeSets.get().stream().noneMatch(cs -> cs.getSqlFile().stream().anyMatch(f -> f.getPath().endsWith("hsqldb_table_2_table_1_11_type_before.sql"))));
        assertTrue(optionalChangeSets.get().stream().noneMatch(cs -> cs.getSqlFile().stream().anyMatch(f -> f.getPath().endsWith("hsqldb_table_1_value_12_type_before.sql"))));

        liquibaseUtils = new LiquibaseUtils(updateDataAfterIncrementalModel.getResourceSet());
        optionalChangeSets = liquibaseUtils.getChangeSets();
        assertTrue(optionalChangeSets.isPresent());
        assertTrue(optionalChangeSets.get().stream().noneMatch(cs -> cs.getSqlFile().stream().anyMatch(f -> f.getPath().endsWith("hsqldb_table_1_value_7_mandatory.sql"))));
        assertTrue(optionalChangeSets.get().stream().noneMatch(cs -> cs.getSqlFile().stream().anyMatch(f -> f.getPath().endsWith("hsqldb_table_1_value_13_type_after.sql"))));

        Connection connection = datasource.getDataSource().getConnection();
        final Database liquibaseDb = datasource.getLiquibaseDb();
        datasource.setLiquibaseDbDialect(connection);

        connection.createStatement().execute("DROP SCHEMA PUBLIC CASCADE");

        runLiquibaseChangeSet(originalLiquibaseModel, liquibaseDb, dialect);
        runLiquibaseChangeSet(dbCheckupModel, liquibaseDb, dialect);
        runLiquibaseChangeSet(dbBackupLiquibaseModel, liquibaseDb, dialect);
        runLiquibaseChangeSet(beforeIncrementalModel, liquibaseDb, dialect);
        runLiquibaseChangeSet(incrementalLiquibaseModel, liquibaseDb, dialect);
        runLiquibaseChangeSet(afterIncrementalModel, liquibaseDb, dialect);
        runLiquibaseChangeSet(dbDropBackupLiquibaseModel, liquibaseDb, dialect);

        liquibaseDb.close();
    }

    private static void replaceTypeNames(final RdbmsField field, final String dialect) {
        final String typeName = field.getRdbmsTypeName();
        if (typeName.equals("Number")) {
            if (dialect.equals(DIALECT_HSQLDB)) {
                field.setRdbmsTypeName("Integer");
                log.info(field.getUuid() + ": Number -> Integer");
            } else if (dialect.equals(DIALECT_POSTGRESQL)) {
                if (field.getPrecision() > 0 || field.getSize() > 0) {
                    field.setRdbmsTypeName("Decimal");
                    log.info(field.getUuid() + ": Number -> Decimal");
                } else {
                    field.setRdbmsTypeName("Integer");
                    log.info(field.getUuid() + ": Number -> Integer");
                }
            } else {
                throw new RuntimeException("Unknown dialect type: " + dialect);
            }
        }
    }

    private static void runLiquibaseChangeSet(LiquibaseModel liquibaseModel, Database liquibaseDb, String dialect) throws LiquibaseException {
        new Liquibase(getLiquibaseFileName(liquibaseModel, dialect),
                      new FileSystemResourceAccessor(TARGET_TEST_CLASSES), liquibaseDb)
                .update("");
    }

    private static void saveRdbms(RdbmsModel rdbmsModel, String dialect) {
        File incrementalRdbmsFile = new File(TARGET_TEST_CLASSES, getRdbmsFileName(rdbmsModel, dialect));
        try {
            rdbmsModel.saveRdbmsModel(rdbmsSaveArgumentsBuilder().file(incrementalRdbmsFile));
        } catch (RdbmsValidationException | IOException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", rdbmsModel.asString(), rdbmsModel.getDiagnosticsAsString()));
        }

    }

    private static String getRdbmsFileName(final RdbmsModel rdbmsModel, String dialect) {
        return "test-" + dialect + "-" + rdbmsModel.getName() + "-rdbms.model";
    }

    private static void saveLiquibase(LiquibaseModel liquibaseModel, String dialect) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            liquibaseModel.saveLiquibaseModel(
                    liquibaseSaveArgumentsBuilder()
                            .outputStream(LiquibaseNamespaceFixUriHandler.fixUriOutputStream(stream)));
        } catch (LiquibaseValidationException | IOException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", liquibaseModel.asString(), liquibaseModel.getDiagnosticsAsString()));
        }
        stream.writeTo(new FileOutputStream(new File(TARGET_TEST_CLASSES, getLiquibaseFileName(liquibaseModel, dialect))));
    }

    private static String getLiquibaseFileName(final LiquibaseModel liquibaseModel, String dialect) {
        return "test-" + dialect + "-" + liquibaseModel.getName() + "-liquibase.xml";
    }

    private URI getUri(Class clazz, String file) throws URISyntaxException {
        URI rdbmsRoot = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (rdbmsRoot.toString().endsWith(".jar")) {
            rdbmsRoot = new URI("jar:" + rdbmsRoot.toString() + "!/" + file);
        } else if (rdbmsRoot.toString().startsWith("jar:bundle:")) {
            rdbmsRoot = new URI(rdbmsRoot.toString().substring(4, rdbmsRoot.toString().indexOf("!")) + file);
        } else {
            rdbmsRoot = new URI(rdbmsRoot.toString() + "/" + file);
        }
        return rdbmsRoot;
    }

    //// EMF Compare experiment
	/*
	 * 		com.google.common.base.Function<EObject, String> idFunction = new com.google.common.base.Function<EObject, String>() {
			@Override
			public @Nullable String apply(@Nullable EObject input) {
				if (input instanceof RdbmsElement) {
					String s = input.getClass().getSimpleName() + 
							((RdbmsElement)input).getUuid();
					System.out.println(s);
					return s;
				} else {
					return null;
				}
			}
		};
		
//		IEObjectMatcher fallBackMatcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
//		IEObjectMatcher customIDMatcher = new IdentifierEObjectMatcher(fallBackMatcher, idFunction);
//		 
//		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
//		 
//		IMatchEngine.Factory.Registry registry = MatchEngineFactoryRegistryImpl.createStandaloneInstance();
//		final MatchEngineFactoryImpl matchEngineFactory = new MatchEngineFactoryImpl(customIDMatcher, comparisonFactory);
//		matchEngineFactory.setRanking(20); // default engine ranking is 10, must be higher to override.
//		registry.add(matchEngineFactory);
//
//		
//    	DefaultComparisonScope scope = new DefaultComparisonScope(newModel.getResource(), originalModel.getResource(), null);
//		Comparison comparison = EMFCompare.builder()
//				.setMatchEngineFactoryRegistry(registry)
//					.build().compare(scope);
//		for (Diff diff : comparison.getDifferences()) {
//			System.out.println(diff);
//		}
    

	 */
}
