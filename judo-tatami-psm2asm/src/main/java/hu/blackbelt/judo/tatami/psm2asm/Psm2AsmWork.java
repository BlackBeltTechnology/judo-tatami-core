package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static java.util.Optional.ofNullable;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;

@Slf4j
public class Psm2AsmWork extends AbstractTransformationWork {

    public static final String PSM_VALIDATON_SCRIPT_URI = "psmValidationScriptUri";

    final URI transformationScriptRoot;

    public Psm2AsmWork(TransformationContext transformationContext, URI transformationScriptRoot) {
        super(transformationContext);
        this.transformationScriptRoot = transformationScriptRoot;
    }

    @Override
    public void execute() throws Exception {
    	
        Optional<PsmModel> psmModel = getTransformationContext().getByClass(PsmModel.class);
        psmModel.orElseThrow(() -> new IllegalArgumentException("PSM Model does not found in transformation context"));
        getTransformationContext().get(URI.class, PSM_VALIDATON_SCRIPT_URI)
        	.ifPresent(validationScriptUri -> {
        		try {
					validatePsm(
						getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
					    psmModel.get(), 
					    validationScriptUri);
				} catch (Exception e) {
					e.printStackTrace();     	
        });

        AsmModel asmModel = getTransformationContext().getByClass(AsmModel.class)
        		.orElseGet(() -> buildAsmModel().name(psmModel.get().getName()).build());
        getTransformationContext().put(asmModel);
        
        Psm2AsmTransformationTrace psm2AsmTransformationTrace = executePsm2AsmTransformation(
                psmModel.get(),
                asmModel,
                getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot);

        getTransformationContext().put(psm2AsmTransformationTrace);
    }
}
