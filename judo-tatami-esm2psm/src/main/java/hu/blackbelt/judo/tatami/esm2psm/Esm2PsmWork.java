package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Optional;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;

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
        Optional<EsmModel> esmModel = getTransformationContext().getByClass(EsmModel.class);
        esmModel.orElseThrow(() -> new IllegalArgumentException("ESM Model does not found in transformation context"));

		/*
		getTransformationContext().get(URI.class, ESM_VALIDATION_SCRIPT_URI)
			.ifPresent(ThrowingConsumer.throwingConsumerWrapper(validationScriptUri ->
				validateEsm( getTransformationContext().getByClass(Log.class).orElseGet(() ->
					new Slf4jLog(log)), esmModel.get(), validationScriptUri)));
		 */

        PsmModel psmModel = getTransformationContext().getByClass(PsmModel.class)
                .orElseGet(() -> buildPsmModel().name(esmModel.get().getName()).build());
        getTransformationContext().put(psmModel);

        Esm2PsmTransformationTrace esm2PsmTransformationTrace = executeEsm2PsmTransformation(esmModel.get(), psmModel,
                getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot);

        getTransformationContext().put(esm2PsmTransformationTrace);
    }
}
