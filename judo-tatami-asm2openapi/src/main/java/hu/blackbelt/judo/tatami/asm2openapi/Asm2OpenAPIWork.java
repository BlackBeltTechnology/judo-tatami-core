package hu.blackbelt.judo.tatami.asm2openapi;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

//import static hu.blackbelt.judo.meta.asm.AsmEpsilonValidator.validateAsm;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;
import static java.util.Optional.ofNullable;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.buildOpenapiModel;

@Slf4j
public class Asm2OpenAPIWork extends AbstractTransformationWork {

    public static final String ASM_VALIDATON_SCRIPT_URI = "AsmValidationScriptUri";

    final URI transformationScriptRoot;

    public Asm2OpenAPIWork(TransformationContext transformationContext, URI transformationScriptRoot) {
        super(transformationContext);
        this.transformationScriptRoot = transformationScriptRoot;
    }

    @Override
    public void execute() throws Exception {
        AsmModel asmModel = getTransformationContext().getByClass(AsmModel.class);
        if (asmModel == null) {
            throw new IllegalArgumentException("ASM Model does not found in transformation context");
        }
        /*
        if (getTransformationContext().get(ASM_VALIDATON_SCRIPT_URI) != null) {
            validateAsm(ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
                    asmModel, (URI) getTransformationContext().get(ASM_VALIDATON_SCRIPT_URI));
        }
		*/
        OpenapiModel openapiModel = getTransformationContext().getByClass(OpenapiModel.class);
        if (openapiModel == null) {
            openapiModel = buildOpenapiModel().name(asmModel.getName()).build();
            getTransformationContext().put(openapiModel);
        }
		
        Asm2OpenAPITransformationTrace asm2OpenapiTransformationTrace = executeAsm2OpenAPITransformation(
                asmModel,
                openapiModel,
                ofNullable((Log) getTransformationContext().get(Log.class)).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot);

        getTransformationContext().put(asm2OpenapiTransformationTrace);
    }
}

