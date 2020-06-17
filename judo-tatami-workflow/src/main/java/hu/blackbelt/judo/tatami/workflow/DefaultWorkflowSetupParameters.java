package hu.blackbelt.judo.tatami.workflow;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms;
import hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK;
import hu.blackbelt.judo.tatami.psm2asm.Psm2Asm;
import hu.blackbelt.judo.tatami.psm2measure.Psm2Measure;
import hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.calculateAsm2JaxrsapiTemplateScriptURI;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.calculateAsm2OpenapiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.calculateAsm2RdbmsModelURI;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.calculateAsm2RdbmsTransformationScriptURI;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.calculateAsm2SDKTemplateScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.calculatePsm2MeasureTransformationScriptURI;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI;

@Builder(builderMethodName = "defaultWorkflowSetupParameters")
@Getter
public class DefaultWorkflowSetupParameters {

	/**
	 * When PsmModel is defined psmModelURI is ignored.
	 */
	private PsmModel psmModel;

	private URI psmModelSourceURI;

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

	@Builder.Default
	private Boolean ignoreAsm2Expression = false;

	@Builder.Default
	private Boolean ignoreAsm2Script = false;

	@Builder.Default
	private Boolean ignoreScript2Operation = false;

	@Builder.Default
	private Boolean validateModels = false;

}
