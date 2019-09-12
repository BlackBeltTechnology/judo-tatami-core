package hu.blackbelt.judo.tatami.workflow;

import java.io.File;
import java.net.URI;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(builderMethodName = "defaultWorkflowSetupParameters")
@Getter
public class DefaultWorkflowSetupParameters {

	@NonNull
	private File psmModelSourceURI;

	@NonNull
	private URI asmModelTransformationScriptRoot;

	@NonNull
	private URI openapiModelTransformationScriptRoot;

	@NonNull
	private URI measureModelTransformationScriptRoot;

	@NonNull
	private URI rdbmsModelTransformationScriptRoot;

	@NonNull
	private URI liquibaseModelTransformationScriptRoot;

	@NonNull
	private String modelName;

	@NonNull
	private String dialect;

	@NonNull
	private URI modelURI;
}
