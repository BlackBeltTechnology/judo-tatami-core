package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import lombok.extern.slf4j.Slf4j;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

	public static void main(String[] args) throws IOException, EsmModel.EsmValidationException, URISyntaxException, PsmModel.PsmValidationException {

		File esmModelFile = new File(args[0]);
		String modelName = args[1];
		File psmModelFile = new File(args[2]);

		Esm2PsmWork esm2PsmWork;
		TransformationContext transformationContext;

		EsmModel esmModel = EsmModel.loadEsmModel(
				esmLoadArgumentsBuilder().file(esmModelFile).name(modelName));

		transformationContext = new TransformationContext(modelName);
		transformationContext.put(esmModel);

		esm2PsmWork = new Esm2PsmWork(transformationContext, Esm2Psm.calculateEsm2PsmTransformationScriptURI());

		WorkFlow workflow = aNewSequentialFlow().execute(esm2PsmWork).build();
		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		PsmModel psmModel = transformationContext.getByClass(PsmModel.class).get();
		psmModel.savePsmModel(psmSaveArgumentsBuilder().file(psmModelFile).build());
	}

}
