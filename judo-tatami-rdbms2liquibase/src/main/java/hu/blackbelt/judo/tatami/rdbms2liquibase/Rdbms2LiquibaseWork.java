package hu.blackbelt.judo.tatami.rdbms2liquibase;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;

import java.net.URI;
import java.util.Optional;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Rdbms2LiquibaseWork extends AbstractTransformationWork {
	
	public static final String RDBMS_VALIDATION_SCRIPT_URI = "rdbmsValidationScriptUri";
	public static final String LIQUIBASE_DIALECT = "liquibase:dialect";
	
	final URI transformationScriptRoot;

	public Rdbms2LiquibaseWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	@Override
	public void execute() throws Exception {
		
		Optional<RdbmsModel> rdbmsModel = getTransformationContext().getByClass(RdbmsModel.class);
		rdbmsModel.orElseThrow(() -> new IllegalArgumentException("RDBMS Model does not found in transformation context"));
		
		registerRdbmsNameMappingMetamodel(rdbmsModel.get().getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.get().getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.get().getResourceSet());
        
        //Does RDBMS require validation?
		/*getTransformationContext().get(URI.class, RDBMS_VALIDATION_SCRIPT_URI)
		.ifPresent(ThrowingConsumer.throwingConsumerWrapper(validationScriptUri -> validateRdbms(
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
			    rdbmsModel.get(), 
			    validationScriptUri)));*/
		
		
		LiquibaseModel liquibaseModel = getTransformationContext().getByClass(LiquibaseModel.class)
        		.orElseGet(() -> buildLiquibaseModel().name(rdbmsModel.get().getName()).build());
		
        getTransformationContext().put(liquibaseModel);
		
		Rdbms2Liquibase.executeRdbms2LiquibaseTransformation(
				rdbmsModel.get(),
				liquibaseModel,
				(Log) getTransformationContext().get(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot, 
				getTransformationContext().get(LIQUIBASE_DIALECT).get().toString());
	}

}
