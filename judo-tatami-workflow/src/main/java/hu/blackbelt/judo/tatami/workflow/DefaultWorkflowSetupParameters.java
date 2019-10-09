package hu.blackbelt.judo.tatami.workflow;

import java.net.URI;
import java.util.List;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(builderMethodName = "defaultWorkflowSetupParameters")
@Getter
public class DefaultWorkflowSetupParameters {

	/**
	 * When PsmModel is defined psmModelURI is ignored.
	 */
	private PsmModel psmModel;
	
	private URI psmModelSourceURI;

	@NonNull
	private URI psm2AsmModelTransformationScriptURI;

	@NonNull
	private URI asm2OpenapiModelTransformationScriptURI;

	@NonNull
	private URI psm2MeasureModelTransformationScriptURI;

	@NonNull
	private URI asm2RdbmsModelTransformationScriptURI;

	@NonNull
	private URI asm2RdbmsModelTransformationModelURI;

	@NonNull
	private URI rdbms2LiquibaseModelTransformationScriptURI;
	
	@NonNull
	private URI asm2sdkModelTransformationScriptURI;
	
	@NonNull
	private URI asm2jaxrsapiModelTransformationScriptURI;

	@NonNull
	private String modelName;

	@NonNull
	private List<String> dialectList;

	@Builder.Default
	private Boolean ignorePsm2Asm = false;

	@Builder.Default
	private Boolean ignorePsm2Measure = false;

	@Builder.Default
	private Boolean ignoreAsm2Openapi = false;

	@Builder.Default
	private Boolean ignoreAsm2Rdbms = false;

	@Builder.Default
	private Boolean ignoreRdbms2Liquibase = false;

	@Builder.Default
	private Boolean ignoreAsm2sdk = false;

	@Builder.Default
	private Boolean ignoreAsm2jaxrsapi = false;

}
