package hu.blackbelt.judo.tatami.asm2keycloak;

import static hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel.buildKeycloakModel;
import static hu.blackbelt.judo.tatami.asm2keycloak.Asm2Keycloak.executeAsm2KeycloakTransformation;

import java.net.URI;
import java.util.Optional;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2KeycloakWork extends AbstractTransformationWork {

	public static final String ASM_VALIDATION_SCRIPT_URI = "asmValidationScriptUri";

	final URI transformationScriptRoot;

	public Asm2KeycloakWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	public Asm2KeycloakWork(TransformationContext transformationContext) {
		this(transformationContext, Asm2Keycloak.calculateAsm2KeycloakTransformationScriptURI());
	}

	@Override
	public void execute() throws Exception {
		Optional<AsmModel> asmModel = getTransformationContext().getByClass(AsmModel.class);
		asmModel.orElseThrow(() -> new IllegalArgumentException("ASM Model does not found in transformation context"));

		KeycloakModel keycloakModel = getTransformationContext().getByClass(KeycloakModel.class)
				.orElseGet(() -> buildKeycloakModel().name(asmModel.get().getName()).build());

		getTransformationContext().put(keycloakModel);

		Asm2KeycloakTransformationTrace asm2KeycloakTransformationTrace = executeAsm2KeycloakTransformation(asmModel.get(),
				keycloakModel, getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot);

		getTransformationContext().put(asm2KeycloakTransformationTrace);
	}
}
