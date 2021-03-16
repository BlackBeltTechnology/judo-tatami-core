package hu.blackbelt.judo.tatami.workflow;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;
import java.util.List;

@Builder(builderMethodName = "defaultWorkflowSetupParameters")
@Getter
public class DefaultWorkflowSetupParameters {

	/**
	 * When PsmModel is defined psmModelURI is ignored.
	 */
	private PsmModel psmModel;

	private URI psmModelSourceURI;

	/**
	 * When EsmModel is defined esmModelURI is ignored.
	 */
	private EsmModel esmModel;

	private URI esmModelSourceURI;

	@NonNull
	private String modelName;

	@NonNull
	private List<String> dialectList;

	@Builder.Default
	private Boolean runInParallel = true;

	@Builder.Default
	private Boolean enableMetrics = true;

	@Builder.Default
	private Boolean ignoreEsm2Psm = false;

	@Builder.Default
	private Boolean ignoreEsm2Ui = false;

	@Builder.Default
	private Boolean ignorePsm2Asm = false;

	@Builder.Default
	private Boolean ignorePsm2Measure = false;

	@Builder.Default
	private Boolean ignoreAsm2Openapi = false;

	@Builder.Default
	private Boolean ignoreAsm2Rdbms = false;

	@Builder.Default
	private Boolean ignoreAsm2Keycloak = false;

	@Builder.Default
	private Boolean ignoreRdbms2Liquibase = false;

	@Builder.Default
	private Boolean ignoreAsm2sdk = false;

	@Builder.Default
	private Boolean ignoreAsm2jaxrsapi = false;

	@Builder.Default
	private Boolean ignoreAsm2Expression = false;

	@Builder.Default
	private Boolean ignoreAsm2Script = false;

	@Builder.Default
	private Boolean ignoreScript2Operation = false;

	@Builder.Default
	private Boolean validateModels = false;

}
