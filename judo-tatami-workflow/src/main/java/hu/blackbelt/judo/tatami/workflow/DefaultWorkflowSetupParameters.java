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
	private File psmModeldest;

	@NonNull
	private URI asmModelURI;

	@NonNull
	private URI openapiModelURI;
	
	@NonNull
	private URI measureModelURI;
	
	@NonNull
	private URI rdbmsModelURI;
	
	@NonNull
	private URI liquibaseModelURI;
	
	@NonNull
	private String modelName;
	
	@NonNull
	private String dialect;
	
	@NonNull
	private String excelModelUri;
}
