package hu.blackbelt.judo.tatami.ui2client;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.executeUi2ClientGeneration;
import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.executeUi2ClientGenerationAsZip;

import java.io.InputStream;
import java.net.URI;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ui2ClientWork extends AbstractTransformationWork {
	
	public static final String CLIENT_OUTPUT = "ui2client:output";

	final URI transformationScriptRoot;

	public Ui2ClientWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	public Ui2ClientWork(TransformationContext transformationContext) {
		this(transformationContext, Ui2Client.calculateUi2ClientTemplateScriptURI());
	}

	@Override
	public void execute() throws Exception {

		UiModel uiModel = getTransformationContext().getByClass(UiModel.class)
				.orElseThrow(() -> new IllegalArgumentException("UI Model does not found in transformation context"));

		InputStream ui2flutterZip = executeUi2ClientGenerationAsZip(uiModel,
//				getTransformationContext().getByClass(ClientTemplateProvider.class).orElseGet(() -> new FlutterTemplateProvider()).get(),
				GeneratorTemplate.loadYamlURL(Ui2Client.calculateUi2ClientTemplateScriptURI().resolve("flutter/flutter.yaml").toURL()),
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot);
		
		checkState(ui2flutterZip != null, "No InputStream created");

		getTransformationContext().put(CLIENT_OUTPUT, ui2flutterZip);

	}

}
