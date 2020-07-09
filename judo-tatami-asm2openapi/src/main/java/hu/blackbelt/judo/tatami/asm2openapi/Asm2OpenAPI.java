package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace.ASM_2_OPENAPI_URI_POSTFIX;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace.resolveAsm2OpenAPITrace;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

@Slf4j
public class Asm2OpenAPI {

    public static final String SCRIPT_ROOT_TATAMI_ASM_2_OPENAPI = "tatami/asm2openapi/transformations/openapi/";

    public static Asm2OpenAPITransformationTrace executeAsm2OpenAPITransformation(AsmModel asmModel, OpenapiModel openAPIModel) throws Exception {
        return executeAsm2OpenAPITransformation(asmModel, openAPIModel, new Slf4jLog(log), calculateAsm2OpenapiTransformationScriptURI());
    }

    public static Asm2OpenAPITransformationTrace executeAsm2OpenAPITransformation(AsmModel asmModel, OpenapiModel openAPIModel, Log log) throws Exception {
        return executeAsm2OpenAPITransformation(asmModel, openAPIModel, log, calculateAsm2OpenapiTransformationScriptURI());
    }

    public static Asm2OpenAPITransformationTrace executeAsm2OpenAPITransformation(AsmModel asmModel, OpenapiModel openAPIModel, Log log,
                                                                                  URI scriptDir) throws Exception {
        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("OPENAPI")
                                .resource(openAPIModel.getResource())
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of("asmUtils", new AsmUtils((asmModel.getResourceSet()))))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("asmToOpenAPI.etl", scriptDir))
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(ASM_2_OPENAPI_URI_POSTFIX, etlExecutionContext);

        return Asm2OpenAPITransformationTrace.asm2OpenAPITransformationTraceBuilder()
                .asmModel(asmModel)
                .openAPIModel(openAPIModel)
                .trace(resolveAsm2OpenAPITrace(traceModel, asmModel, openAPIModel)).build();
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateAsm2OpenapiTransformationScriptURI() {
        URI psmRoot = Asm2OpenAPI.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_ASM_2_OPENAPI);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_ASM_2_OPENAPI);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_ASM_2_OPENAPI);
        }
        return psmRoot;
    }

}
