package hu.blackbelt.judo.tatami.ui2client;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.LoadArguments.uiLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.ui2client.flutter.FlutterHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ui2ClientWork extends AbstractTransformationWork {
	
	public static final String CLIENT_OUTPUT = "ui2client:output";
	public static final String OVERRDED_URIS = "ui2client:overrided-uris";

	public Ui2ClientWork(TransformationContext transformationContext) {
		super(transformationContext);
	}

	@Override
	public void execute() throws Exception {

		UiModel uiModel = getTransformationContext().getByClass(UiModel.class)
				.orElseThrow(() -> new IllegalArgumentException("UI Model does not found in transformation context"));

		List<URI> overridedURIS = getTransformationContext().get(List.class, OVERRDED_URIS)
				.orElseGet(() -> Collections.EMPTY_LIST);

		Map<Application, InputStream> ui2flutterZip = executeUi2ClientGenerationAsZip(Ui2FlutterClient.getFlutterClientGenerator(uiModel, overridedURIS),
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)));
		
		checkState(ui2flutterZip != null, "No InputStream created");

		getTransformationContext().put(CLIENT_OUTPUT, ui2flutterZip);

	}

}
