package hu.blackbelt.judo.tatami.openapi2restclient;

import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.tatami.openapi2restclient.Openapi2RestClient.executeOpenaapi2RestClientGeneration;

import java.io.File;

@Slf4j
public class Openapi2RestClientWork extends AbstractTransformationWork {

	public static final String GENERATED_CLIENT_OUTPUT = "openapirestclients:";

	private String language;
	private Map<String, Object> opts;

	public Openapi2RestClientWork(TransformationContext transformationContext, String language, Map<String, Object> opts) {
		super(transformationContext);
		this.language = language;
		this.opts = opts;
	}

	@Override
	public void execute() throws Exception {

		Optional<OpenapiModel> openapiModel = getTransformationContext().getByClass(OpenapiModel.class);
		openapiModel.orElseThrow(() -> new IllegalArgumentException("penAPI Model does not found in transformation context"));

		File temporaryDirectory = File.createTempFile(Openapi2RestClientWork.class.getName(), openapiModel.get().getName());
		if (temporaryDirectory.exists()) {
			temporaryDirectory.delete();
		}
		temporaryDirectory.deleteOnExit();
		temporaryDirectory.mkdir();

		Map<API, Map<String, File>> openapi2restclients = Openapi2RestClient.executeOpenaapi2RestClientGeneration(openapiModel.get(), language,
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				temporaryDirectory, opts);

		checkState(openapi2restclients != null, "No files created");
		getTransformationContext().put(GENERATED_CLIENT_OUTPUT + language, openapi2restclients);

	}
}
