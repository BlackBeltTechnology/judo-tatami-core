package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Asm2OpenAPI {

    public static final String ASM_2_OPENAPI_URI_POSTFIX = "asm2openapi";

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


    public static ResourceSet createAsm2OpenAPITraceResourceSet() {
        return createTraceResourceSet(ASM_2_OPENAPI_URI_POSTFIX);
    }

    public static Map<EObject, List<EObject>> resolveAsm2OpenAPITrace(Resource traceResource, AsmModel asmModel, OpenapiModel openAPIModel) {
        return resolveAsm2OpenAPITrace(traceResource.getContents(), asmModel, openAPIModel);
    }

    public static Map<EObject, List<EObject>> resolveAsm2OpenAPITrace(List<EObject> trace, AsmModel asmModel, OpenapiModel openAPIModel) {
        return resolveTransformationTraceAsEObjectMap(trace,
                ImmutableList.of(asmModel.getResourceSet(), openAPIModel.getResourceSet()));
    }

    public static List<EObject> getAsm2OpenAPITrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTraceFromEtlExecutionContext(ASM_2_OPENAPI_URI_POSTFIX, trace);
    }
}
