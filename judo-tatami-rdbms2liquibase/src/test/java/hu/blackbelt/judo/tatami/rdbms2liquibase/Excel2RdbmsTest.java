package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseNamespaceFixUriHandler;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsUtils;
import hu.blackbelt.judo.meta.rdbms.util.builder.RdbmsIndexBuilder;
import hu.blackbelt.judo.meta.rdbms.util.builder.RdbmsUniqueConstraintBuilder;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.HsqlConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.eclipse.epsilon.common.util.UriUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsIncremental.transformRdbmsIncrementalModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.buildRdbmsModel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseIncremental.executeRdbms2LiquibaseIncrementalTransformation;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.fail;

public class Excel2RdbmsTest {

    private static final String ORIGINAL_MODEL_NAME = "OriginalModel";
    private static final String TARGET_TEST_CLASSES = "target/test-classes";

    @Test
    public void executeExcel2RdbmsModel() throws Exception {

        RdbmsModel originalModel = buildRdbmsModel().name(ORIGINAL_MODEL_NAME).build();
        RdbmsModel newModel = buildRdbmsModel().name("NewModel").build();

        // Execution context
        ExecutionContext excelToRdbmsEtlContext = executionContextBuilder()
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

        excelToRdbmsEtlContext.executeProgram(etlExecutionContextBuilder().source(UriUtil.resolve("createExcelModel.etl", testRoot)).build());

        excelToRdbmsEtlContext.commit();
        excelToRdbmsEtlContext.close();

//        final RdbmsUtils rdbmsUtils = new RdbmsUtils(originalModel.getResourceSet());
//        final RdbmsTable rdbmsTable = rdbmsUtils.getRdbmsTables().get().get(0);
//        rdbmsTable.getIndexes().add(
//                RdbmsIndexBuilder.create()
//                        .withName("TestIndex")
//                        .withUuid(rdbmsTable.getUuid() + ".TestIndex")
//                        .withFields(rdbmsTable.getFields().get(0), rdbmsTable.getFields().get(1))
//                        .withUnique(true)
//                        .withSqlName("TestIndex".toUpperCase())
//                        .build());
//        rdbmsTable.getUniqueConstraints().add(
//                RdbmsUniqueConstraintBuilder.create()
//                        .withName("TestUniqueConstraint")
//                        .withUuid(rdbmsTable.getUuid() + ".TestUniqueConstraint")
//                        .withFields(rdbmsTable.getFields().get(2), rdbmsTable.getFields().get(3))
//                        .withSqlName("TestUniqueConstraint".toUpperCase())
//                        .build());
//
//        final RdbmsUtils rdbmsUtils2 = new RdbmsUtils(newModel.getResourceSet());
//        final RdbmsTable rdbmsTable2 = rdbmsUtils2.getRdbmsTables().get().get(0);
//        rdbmsTable2.getIndexes().add(
//                RdbmsIndexBuilder.create()
//                        .withName("TestIndex")
//                        .withUuid(rdbmsTable2.getUuid() + ".TestIndex")
//                        .withUnique(true)
//                        .withFields(rdbmsTable2.getFields().get(0), rdbmsTable2.getFields().get(1))
//                        .withSqlName("TestIndex".toUpperCase())
//                        .build());
//        rdbmsTable2.getUniqueConstraints().add(
//                RdbmsUniqueConstraintBuilder.create()
//                        .withName("TestUniqueConstraint")
//                        .withUuid(rdbmsTable2.getUuid() + ".TestUniqueConstraint")
//                        .withFields(rdbmsTable2.getFields().get(2), rdbmsTable2.getFields().get(3))
//                        .withSqlName("TestUniqueConstraint".toUpperCase())
//                        .build());

        saveRdbms(originalModel);
        saveRdbms(newModel);

        // fill models
        /////////////////////////////////////////
        // rdbms2liquibase (original)
        LiquibaseModel originalLiquibaseModel = buildLiquibaseModel().name(ORIGINAL_MODEL_NAME).build();
        executeRdbms2LiquibaseTransformation(originalModel, originalLiquibaseModel, "hsqldb");

        saveLiquibase(originalLiquibaseModel);

        // rdbms2liquibase (original)
        /////////////////////////////////////////
        // delta model

        RdbmsModel incrementalModel = buildRdbmsModel().name("IncrementalModel").build();
        transformRdbmsIncrementalModel(originalModel, newModel, incrementalModel, "hsqldb", true);

        saveRdbms(incrementalModel);

        LiquibaseModel dbCheckupModel = buildLiquibaseModel().name("DbCheckup").build();
        LiquibaseModel dbBackupLiquibaseModel = buildLiquibaseModel().name("DbBackup").build();
        LiquibaseModel beforeIncrementalModel = buildLiquibaseModel().name("BeforeIncremental").build();
        LiquibaseModel incrementalLiquibaseModel = buildLiquibaseModel().name("IncrementalModel").build();
        LiquibaseModel afterIncrementalModel = buildLiquibaseModel().name("AfterIncremental").build();
        LiquibaseModel dbDropBackupLiquibaseModel = buildLiquibaseModel().name("DbDropBackup").build();

        executeRdbms2LiquibaseIncrementalTransformation(
                incrementalModel,
                dbCheckupModel,
                dbBackupLiquibaseModel,
                beforeIncrementalModel,
                incrementalLiquibaseModel,
                afterIncrementalModel,
                dbDropBackupLiquibaseModel,
                "hsqldb");

        saveLiquibase(dbCheckupModel);
        saveLiquibase(dbBackupLiquibaseModel);
        saveLiquibase(beforeIncrementalModel);
        saveLiquibase(incrementalLiquibaseModel);
        saveLiquibase(afterIncrementalModel);
        saveLiquibase(dbDropBackupLiquibaseModel);

        // $ java -cp hsqldb/lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:mydb --dbname.0 xdb
//        Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/xdb", "SA", "");
//        connection.createStatement().execute("DROP SCHEMA PUBLIC CASCADE");
//
//        Database liquibaseDb = new HsqlDatabase();
//        liquibaseDb.setConnection(new HsqlConnection(connection));
//
//        runLiquibaseChangeSet(originalLiquibaseModel, liquibaseDb);
//        runLiquibaseChangeSet(dbCheckupModel, liquibaseDb);
//        runLiquibaseChangeSet(dbBackupLiquibaseModel, liquibaseDb);
//        runLiquibaseChangeSet(beforeIncrementalModel, liquibaseDb);
//        runLiquibaseChangeSet(incrementalLiquibaseModel, liquibaseDb);
//        runLiquibaseChangeSet(afterIncrementalModel, liquibaseDb);
//        runLiquibaseChangeSet(dbDropBackupLiquibaseModel, liquibaseDb);
//
//        liquibaseDb.close();
    }

    private void runLiquibaseChangeSet(LiquibaseModel liquibaseModel, Database liquibaseDb) throws LiquibaseException {
        new Liquibase(getLiquibaseFileName(liquibaseModel),
                      new FileSystemResourceAccessor(TARGET_TEST_CLASSES), liquibaseDb)
                .update("");
    }

    private void saveRdbms(RdbmsModel rdbmsModel) {
        File incrementalRdbmsFile = new File(TARGET_TEST_CLASSES, getRdbmsFileName(rdbmsModel));
        try {
            rdbmsModel.saveRdbmsModel(rdbmsSaveArgumentsBuilder().file(incrementalRdbmsFile));
        } catch (RdbmsValidationException | IOException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", rdbmsModel.asString(), rdbmsModel.getDiagnosticsAsString()));
        }

    }

    private static String getRdbmsFileName(final RdbmsModel rdbmsModel) {
        return "test-" + rdbmsModel.getName() + "-rdbms.model";
    }

    private void saveLiquibase(LiquibaseModel liquibaseModel) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            liquibaseModel.saveLiquibaseModel(
                    liquibaseSaveArgumentsBuilder()
                            .outputStream(LiquibaseNamespaceFixUriHandler.fixUriOutputStream(stream)));
        } catch (LiquibaseValidationException | IOException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", liquibaseModel.asString(), liquibaseModel.getDiagnosticsAsString()));
        }
        stream.writeTo(new FileOutputStream(new File(TARGET_TEST_CLASSES, getLiquibaseFileName(liquibaseModel))));
    }

    private static String getLiquibaseFileName(final LiquibaseModel liquibaseModel) {
        return "test-" + liquibaseModel.getName() + "-liquibase.xml";
    }

    private URI getUri(Class clazz, String file) throws URISyntaxException {
        URI psmRoot = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + file);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + file);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + file);
        }
        return psmRoot;
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
