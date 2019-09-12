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
	private URI psm2asmModelTransformationScriptRoot;

	@NonNull
	private URI asm2openapiModelTransformationScriptRoot;

	@NonNull
	private URI psm2measureModelTransformationScriptRoot;

	@NonNull
	private URI asm2rdbmsModelTransformationScriptRoot;

	@NonNull
	private URI rdbms2liquibaseModelTransformationScriptRoot;

	@NonNull
	private String modelName;

	@NonNull
	private String dialect;

	@NonNull
	private URI modelURI;
}
