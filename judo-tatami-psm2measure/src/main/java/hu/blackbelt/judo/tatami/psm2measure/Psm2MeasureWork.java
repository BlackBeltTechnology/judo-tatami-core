package hu.blackbelt.judo.tatami.psm2measure;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Optional;

import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.buildMeasureModel;

@Slf4j
public class Psm2MeasureWork extends AbstractTransformationWork {

	public static final String PSM_VALIDATION_SCRIPT_URI = "psmValidationScriptUri";
	
	final URI transformationScriptRoot;
	
	public Psm2MeasureWork(TransformationContext transformationContext, URI transformationScriptRoot) {
        super(transformationContext);
        this.transformationScriptRoot = transformationScriptRoot;
    }
	
	@Override
	public void execute() throws Exception {
		
		Optional<PsmModel> psmModel = getTransformationContext().getByClass(PsmModel.class);
		psmModel.orElseThrow(() -> new IllegalArgumentException("PSM Model does not found in transformation context"));
		getTransformationContext().get(URI.class, PSM_VALIDATION_SCRIPT_URI)
			.ifPresent(validationScriptUri -> {
				try {
					validatePsm(
						getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
					    psmModel.get(), 
					    validationScriptUri);
				} catch (Exception e) {
                    e.printStackTrace();
                }
			});
		
		MeasureModel measureModel = getTransformationContext().getByClass(MeasureModel.class)
				.orElseGet(() -> buildMeasureModel().name(psmModel.get().getName()).build());
		getTransformationContext().put(measureModel);
		
		Psm2MeasureTransformationTrace psm2measureTransformationTrace = executePsm2MeasureTransformation(
				psmModel.get(),
				measureModel,
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot);
		
		getTransformationContext().put(psm2measureTransformationTrace);
		
	}
}