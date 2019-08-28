package hu.blackbelt.judo.tatami.rdbms2liquibase;

import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static java.util.Optional.ofNullable;

import java.net.URI;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

//import static hu.blackbelt.judo.meta.rdbms.RdbmsEpsilonValidator.validateRdbms;

@Slf4j
public class Rdbms2LiquibaseWork extends AbstractTransformationWork {
	
	public static final String RDBMS_VALIDATON_SCRIPT_URI = "rdbmsValidationScriptUri";
	public static final String LIQUIBASE_DIALECT = "liquibase:dialect";
	
	final URI transformationScriptRoot;

	public Rdbms2LiquibaseWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	@Override
	public void execute() throws Exception {
		
		RdbmsModel rdbmsModel = getTransformationContext().getByClass(RdbmsModel.class);

		if(rdbmsModel == null) throw new IllegalArgumentException("RDBMS Model does not found in transformation context");
		
		//RDBMS validator is missing!!
		if(getTransformationContext().get(RDBMS_VALIDATON_SCRIPT_URI) != null)
		{
			/*validateRdbms(ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
                    rdbmsModel, (URI) getTransformationContext().get(RDBMS_VALIDATON_SCRIPT_URI));*/
		}
		
		LiquibaseModel liquibaseModel = getTransformationContext().getByClass(LiquibaseModel.class);
		
		if(liquibaseModel == null)
		{
			liquibaseModel = LiquibaseModel.buildLiquibaseModel().name(rdbmsModel.getName()).build();
			getTransformationContext().put(liquibaseModel);
		}
		
		Rdbms2Liquibase.executeRdbms2LiquibaseTransformation(rdbmsModel, liquibaseModel,
				ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot
				, getTransformationContext().get(LIQUIBASE_DIALECT).toString());
	}

}
