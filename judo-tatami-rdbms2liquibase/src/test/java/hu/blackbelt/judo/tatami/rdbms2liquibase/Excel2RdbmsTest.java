package hu.blackbelt.judo.tatami.rdbms2liquibase;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext;
import hu.blackbelt.judo.meta.rdbms.RdbmsElement;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;

public class Excel2RdbmsTest {

	public static final String SCRIPT_ROOT_TATAMI_TRANSFORMATION = "tatami/rdbms2liquibase/transformations/";

	@Test
	public void executeExcel2RdbmsModel() throws Exception {

        RdbmsModel originalModel = RdbmsModel.buildRdbmsModel()
                .name("OriginalModel")
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
                			.aliases(Arrays.asList("XLS"))
                			.excel(getUri(this.getClass(), "RdbmsIncrementalTests.xlsx").toString())
                			.excelConfiguration(getUri(this.getClass(), "mapping.xml").toString())
                			.build(),
                        wrappedEmfModelContextBuilder()
                                .name("ORIGINAL_MODEL")
                                .aliases(Arrays.asList("ORIGINAL"))
                                .resource(originalModel.getResource())
                                .build(),
		                wrappedEmfModelContextBuilder()
		                .name("NEW_MODEL")
		                .aliases(Arrays.asList("NEW"))
		                .resource(newModel.getResource())
		                .build()))

                .build();
        
		excelToRdbmsEtlContext.load();

        URI psmRoot = getUri(Rdbms2Liquibase.class, SCRIPT_ROOT_TATAMI_TRANSFORMATION);

		excelToRdbmsEtlContext
				.executeProgram(etlExecutionContextBuilder().source(UriUtil.resolve("createExcelModel.etl", psmRoot))
						.build());

		excelToRdbmsEtlContext.commit();
		excelToRdbmsEtlContext.close();
		
    	File originalRdbmsFile = new File("target/test-classes", String.format("testContents-%s-rdbms.model",  originalModel.getName()));
		originalModel.saveRdbmsModel(
    			RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(originalRdbmsFile)
    			);
    	
    	File newRdbmsFile = new File("target/test-classes", String.format("testContents-%s-rdbms.model",  newModel.getName()));
		newModel.saveRdbmsModel(
    			RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(newRdbmsFile)
    			);
    	
        RdbmsModel incrementalModel = RdbmsModel.buildRdbmsModel()
                .name("IncrementalModel")
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

		incrementalOperationEtlContext
				.executeProgram(etlExecutionContextBuilder().source(UriUtil.resolve("createIncrementalOperationModel.etl", psmRoot))
						.build());

		incrementalOperationEtlContext.commit();
		incrementalOperationEtlContext.close();

    	File incrementalRdbmsFile = new File("target/test-classes", String.format("testContents-%s-rdbms.model",  incrementalModel.getName()));
    	incrementalModel.saveRdbmsModel(
    			RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder().file(incrementalRdbmsFile)
    			);

    	
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
