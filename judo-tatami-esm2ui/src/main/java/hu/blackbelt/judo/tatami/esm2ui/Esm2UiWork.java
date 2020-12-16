package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import lombok.extern.slf4j.Slf4j;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.LoadArguments.esmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;

@Slf4j
public class Esm2UiWork extends AbstractTransformationWork {

    public static final String ESM_VALIDATION_SCRIPT_URI = "esmValidationScriptUri";

    final URI transformationScriptRoot;

    public Esm2UiWork(TransformationContext transformationContext, URI transformationScriptRoot) {
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

        UiModel uiModel = getTransformationContext().getByClass(UiModel.class)
                .orElseGet(() -> buildUiModel().name(esmModel.get().getName()).build());
        getTransformationContext().put(uiModel);

        Esm2UiTransformationTrace esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel.get(), 
                getTransformationContext().get(String.class, "applicationType").orElseGet(() -> "default"),
                getTransformationContext().get(Integer.class, "applicationColumns").orElseGet(() -> new Integer(12)),
                uiModel,
        		getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot);

		getTransformationContext().put(esm2UiTransformationTrace);
	}

	public static void main(String[] args) throws Exception {

		File esmModelFile = new File(args[0]);
		String modelName = args[1];
		File uiModelFile = new File(args[2]);

		EsmModel esmModel = EsmModel.loadEsmModel(
				esmLoadArgumentsBuilder().validateModel(true).file(esmModelFile).name(modelName));

		UiModel uiModel = buildUiModel().name(esmModel.getName()).build();

		executeEsm2UiTransformation(esmModel,
				"default",
				 new Integer(12),
				uiModel,
				new Slf4jLog(log),
				Esm2Ui.calculateEsm2UiTransformationScriptURI());

		uiModel.saveUiModel(uiSaveArgumentsBuilder().file(uiModelFile).build());

		/*
		Esm2UiWork esm2UiWork;
		TransformationContext transformationContext;

		EsmModel esmModel = EsmModel.loadEsmModel(
				esmLoadArgumentsBuilder().validateModel(true).file(esmModelFile).name(modelName));

		transformationContext = new TransformationContext(modelName);
		transformationContext.put(esmModel);
		transformationContext.put(new Slf4jLog(LoggerFactory.getLogger(Esm2UiWork.class)));

		esm2UiWork = new Esm2UiWork(transformationContext, Esm2Ui.calculateEsm2UiTransformationScriptURI());

		WorkFlow workflow = aNewSequentialFlow().execute(esm2UiWork).build();
		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		UiModel uiModel = transformationContext.getByClass(UiModel.class).get();
		uiModel.saveUiModel(uiSaveArgumentsBuilder().file(uiModelFile).build()); */
	}

}
