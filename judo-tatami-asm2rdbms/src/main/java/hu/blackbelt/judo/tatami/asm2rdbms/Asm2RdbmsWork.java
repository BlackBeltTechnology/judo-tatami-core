package hu.blackbelt.judo.tatami.asm2rdbms;

import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
//import static hu.blackbelt.judo.meta.asm.AsmEpsilonValidator.validateAsm;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.executeAsm2RdbmsTransformation;
import static java.util.Optional.ofNullable;

import java.net.URI;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2RdbmsWork extends AbstractTransformationWork {
	
	public static final String ASM_VALIDATION_SCRIPT_URI = "asmValidationScriptUri";
	
	public static final String RDBMS_EXCELMODEURI = "asm2Rdbms.excelModeUri";
	public static final String RDBMS_DIALECT = "asm2Rdbms.dialect";

    final URI transformationScriptRoot;

	public Asm2RdbmsWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	@Override
	public void execute() throws Exception {
		AsmModel asmModel = getTransformationContext().getByClass(AsmModel.class);

		if(asmModel == null) throw new IllegalArgumentException("ASM Model does not found in transformation context");
	
		//Asm validator not yet implemented
		if (getTransformationContext().get(ASM_VALIDATION_SCRIPT_URI) != null) {
            /*validateAsm(ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
                    asmModel, (URI) getTransformationContext().get(ASM_VALIDATON_SCRIPT_URI));*/
        }
		
		RdbmsModel rdbmsModel = getTransformationContext().getByClass(RdbmsModel.class);
		
		if (rdbmsModel == null) {
            rdbmsModel = RdbmsModel.buildRdbmsModel().name(asmModel.getName()).build();
            // The RDBMS model resources have to know the mapping models
            registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
            registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
            registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());
            getTransformationContext().put(rdbmsModel);
        }
		
		Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace = executeAsm2RdbmsTransformation(asmModel,rdbmsModel,
				ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot,
				(URI)getTransformationContext().get(RDBMS_EXCELMODEURI),
				getTransformationContext().get(RDBMS_DIALECT).toString());
		
		getTransformationContext().put(asm2RdbmsTransformationTrace);
	}

}
