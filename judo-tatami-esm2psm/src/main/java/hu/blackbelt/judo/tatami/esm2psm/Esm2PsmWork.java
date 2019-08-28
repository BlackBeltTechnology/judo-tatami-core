package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;

//import static hu.blackbelt.judo.meta.esm.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static java.util.Optional.ofNullable;

import java.net.URI;

@Slf4j
public class Esm2PsmWork extends AbstractTransformationWork {
	
	public static final String ESM_VALIDATION_SCRIPT_URI = "esmValidationScriptUri";
	
	final URI transformationScriptRoot;

	public Esm2PsmWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	@Override
	public void execute() throws Exception {
		EsmModel esmModel = getTransformationContext().getByClass(EsmModel.class);
		
		if(esmModel == null) throw new IllegalArgumentException("ESM Model not found in the transformation context");
		
		//Esm Validator is missing
		if(getTransformationContext().get(ESM_VALIDATION_SCRIPT_URI) != null)
		{
			/*validateEsm(ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
                    esmModel, (URI) getTransformationContext().get(ESM_VALIDATION_SCRIPT_URI));*/
		}
		
		PsmModel psmModel = getTransformationContext().getByClass(PsmModel.class);
		
		if(psmModel == null)
		{
			psmModel = PsmModel.buildPsmModel().name(esmModel.getName()).build();
			getTransformationContext().put(psmModel);
		}
		
		Esm2PsmTransformationTrace esm2PsmTransformationTrace = executeEsm2PsmTransformation(esmModel,psmModel,
				ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot);
		
		getTransformationContext().put(esm2PsmTransformationTrace);
	}

}
