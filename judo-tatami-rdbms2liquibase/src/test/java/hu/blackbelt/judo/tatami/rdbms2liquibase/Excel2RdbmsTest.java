package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseNamespaceFixUriHandler;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import org.eclipse.epsilon.common.util.UriUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.fail;

public class Excel2RdbmsTest {

    private static final String INCREMENTAL_MODEL_NAME = "IncrementalModel";
    private static final String ORIGINAL_MODEL_NAME = "OriginalModel";
    private static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String SCRIPT_ROOT_TATAMI_TRANSFORMATION = "tatami/rdbms2liquibase/transformations/";

    @Test
    public void executeExcel2RdbmsModel() throws Exception {

        RdbmsModel originalModel = RdbmsModel.buildRdbmsModel()
                .name(ORIGINAL_MODEL_NAME)
                .build();

        RdbmsModel newModel = RdbmsModel.buildRdbmsModel()
                .name("NewModel")
                .build();

        // Execution context
        ExecutionContext excelToRdbmsEtlContext = executionContextBuilder()
                .resourceSet(originalModel.getResourceSet())
                .modelContexts(ImmutableList.of(
                        ExcelModelContext.excelModelContextBuilder()
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

        URI psmRoot = getUri(Rdbms2Liquibase.class, SCRIPT_ROOT_TATAMI_TRANSFORMATION);

        excelToRdbmsEtlContext.executeProgram(etlExecutionContextBuilder().source(UriUtil.resolve("createExcelModel.etl", psmRoot)).build());

        excelToRdbmsEtlContext.commit();
        excelToRdbmsEtlContext.close();

        File originalRdbmsFile = new File(TARGET_TEST_CLASSES, String.format("testContents-%s-rdbms.model", originalModel.getName()));
        try {
            originalModel.saveRdbmsModel(RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(originalRdbmsFile));
        } catch (RdbmsValidationException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", originalModel.asString(), originalModel.getDiagnosticsAsString()));
        }

        File newRdbmsFile = new File(TARGET_TEST_CLASSES, String.format("testContents-%s-rdbms.model", newModel.getName()));
        try {
            newModel.saveRdbmsModel(RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(newRdbmsFile));
        } catch (RdbmsValidationException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", newModel.asString(), newModel.getDiagnosticsAsString()));
        }

        RdbmsModel incrementalModel = RdbmsModel.buildRdbmsModel()
                .name(INCREMENTAL_MODEL_NAME)
                .build();

        ExecutionContext incrementalOperationEtlContext = executionContextBuilder()
                .resourceSet(originalModel.getResourceSet())
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .name("PREVIOUS")
                                .aliases(Arrays.asList("SOURCE", "RDBMS")) // needed for common eols
                                .resource(originalModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("NEW")
                                .aliases(Arrays.asList("SOURCE", "RDBMS"))
                                .resource(newModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("INCREMENTAL")
                                .resource(incrementalModel.getResource())
                                .build()))
                .build();

        incrementalOperationEtlContext.load();

        incrementalOperationEtlContext.executeProgram(
                etlExecutionContextBuilder().source(UriUtil.resolve("createIncrementalOperationModel.etl", psmRoot)).build());

        incrementalOperationEtlContext.commit();
        incrementalOperationEtlContext.close();

        File incrementalRdbmsFile = new File(TARGET_TEST_CLASSES, String.format("testContents-%s-rdbms.model", incrementalModel.getName()));
        try {
            incrementalModel.saveRdbmsModel(RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(incrementalRdbmsFile));
        } catch (RdbmsValidationException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", incrementalModel.asString(), incrementalModel.getDiagnosticsAsString()));
        }

        // save old rdmbs model to liquibase changelog
        LiquibaseModel originalLiquibaseModel = buildLiquibaseModel()
                .name(ORIGINAL_MODEL_NAME)
                .build();

        Rdbms2Liquibase.executeRdbms2LiquibaseTransformation(originalModel, originalLiquibaseModel, "hsqldb");

        ByteArrayOutputStream originalLiquibaseStream = new ByteArrayOutputStream();
        try {
            originalLiquibaseModel.saveLiquibaseModel(
                    LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder()
                            .outputStream(LiquibaseNamespaceFixUriHandler.fixUriOutputStream(originalLiquibaseStream)));
        } catch (LiquibaseValidationException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", originalLiquibaseModel.asString(), originalLiquibaseModel.getDiagnosticsAsString()));
        }
        String originalLiquibaseName = format("testContents-%s-liquibase.xml", originalLiquibaseModel.getName());
        originalLiquibaseStream.writeTo(new FileOutputStream(new File(TARGET_TEST_CLASSES, originalLiquibaseName)));

//        Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001", "SA", "");
//        Database liquibaseDb = new HsqlDatabase();
//        liquibaseDb.setConnection(new HsqlConnection(connection));
//
//        Liquibase originalLiquibase = new Liquibase(originalLiquibaseName,
//                                                    new FileSystemResourceAccessor(TARGET_TEST_CLASSES), liquibaseDb);
//        originalLiquibase.update("");
//        liquibaseDb.close();

        // save incremental model to liquibase changelog

        LiquibaseModel incrementalLiquibaseModel = buildLiquibaseModel()
                .name(INCREMENTAL_MODEL_NAME)
                .build();

        Rdbms2Liquibase.executeRdbms2LiquibaseTransformation(incrementalModel, incrementalLiquibaseModel, "hsqldb");

        ByteArrayOutputStream incrementalLiquibaseStream = new ByteArrayOutputStream();
        try {
            incrementalLiquibaseModel.saveLiquibaseModel(
                    LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder()
                            .outputStream(LiquibaseNamespaceFixUriHandler.fixUriOutputStream(incrementalLiquibaseStream)));
        } catch (LiquibaseValidationException ex) {
            fail(format("Model:\n%s\nDiagnostic:\n%s", incrementalLiquibaseModel.asString(), incrementalLiquibaseModel.getDiagnosticsAsString()));
        }
        String incrementalLiquibaseName = format("testContents-%s-liquibase.xml", incrementalLiquibaseModel.getName());
        incrementalLiquibaseStream.writeTo(new FileOutputStream(new File(TARGET_TEST_CLASSES, incrementalLiquibaseName)));

//        connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001", "SA", "");
//        liquibaseDb = new HsqlDatabase();
//        liquibaseDb.setConnection(new HsqlConnection(connection));
//
//        Liquibase incrementalLiquibase = new Liquibase(incrementalLiquibaseName,
//                                                       new FileSystemResourceAccessor(TARGET_TEST_CLASSES), liquibaseDb);
//        incrementalLiquibase.update("");
//        liquibaseDb.close();

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
