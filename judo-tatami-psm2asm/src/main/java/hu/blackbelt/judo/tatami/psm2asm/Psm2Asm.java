package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;
import static org.eclipse.emf.common.util.URI.createURI;

public class Psm2Asm {

    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    public static final String PSM_2_ASM_URI_POSTFIX = "psm2asm";

    /**
     * Execute PSM to ASM model transformation,
     * @param psmModel The PSM model definition and loaded resources
     * @param asmModel The asm model definition (target)
     * @param log The log instance used in scripts
     * @param scriptUri The URI base where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Psm2AsmTransformationTrace executePsm2AsmTransformation(PsmModel psmModel, AsmModel asmModel, Log log,
                                                                          URI scriptUri) throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("JUDOPSM")
                                .resource(psmModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResource())
                                .build()))
                .injectContexts(ImmutableMap.of(
                        "asmUtils", new AsmUtils(asmModel.getResourceSet()),
                        "psmUtils", new PsmUtils()
                )).build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("psmToAsm.etl", scriptUri))
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelName").value(psmModel.getName()).build(),
                        programParameterBuilder().name("nsURI").value("http://blackbelt.hu/judo/" + psmModel.getName()).build(),
                        programParameterBuilder().name("nsPrefix").value("runtime" + psmModel.getName()).build(),
                        programParameterBuilder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);
        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(PSM_2_ASM_URI_POSTFIX, etlExecutionContext);
        return Psm2AsmTransformationTrace.psm2AsmTransformationTraceBuilder()
                .asmModel(asmModel)
                .psmModel(psmModel)
                .trace(resolvePsm2AsmTrace(traceModel, psmModel, asmModel)).build();
    }

    /**
     * Create PSM 2 ASM Trace model {@link Resource} wth isolated {@link ResourceSet}
     *
     * @return the trace {@link Resource} with the registered namespace.
     */
    public static Resource createPsm2AsmTraceResource(org.eclipse.emf.common.util.URI uri,
                                                      URIHandler uriHandler) {
        return createTraceModelResource(PSM_2_ASM_URI_POSTFIX, uri, uriHandler);
    }

    /**
     * Resolves PSM 2 ASM Trace model {@link Resource} and resturns the trace {@link EObject } map
     *
     * @return the trace {@link EObject} map between PSM source and ASM target.
     */
    public static Map<EObject, List<EObject>> resolvePsm2AsmTrace(Resource traceResource,
                                                                  PsmModel psmModel,
                                                                  AsmModel asmModel) {
        return resolvePsm2AsmTrace(traceResource.getContents(), psmModel, asmModel);
    }

    /**
     * Resolves PSM 2 ASM trace:Trace model entries returns the trace {@link EObject } map
     *
     * @return the trace {@link EObject} map between PSM source and ASM target.
     */
    public static Map<EObject, List<EObject>> resolvePsm2AsmTrace(List<EObject> trace,
                                                                  PsmModel psmModel,
                                                                  AsmModel asmModel) {
        return resolveTransformationTraceAsEObjectMap(trace,
                ImmutableList.of(psmModel.getResourceSet(), asmModel.getResourceSet()));
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model entrie.
     *
     * @return the trace trace:Trace entries
     */
    public static List<EObject> getPsm2AsmTrace(Map<EObject, List<EObject>> trace) {
        return getTransformationTraceFromEtlExecutionContext(PSM_2_ASM_URI_POSTFIX, trace);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @return the trace trace:Trace entries
     */
    public static Resource getPsm2AsmTraceResource(Map<EObject, List<EObject>> trace,
                                                   org.eclipse.emf.common.util.URI modelUri,
                                                   URIHandler uriHandler) {
        return createTraceModelResourceFromEObjectMap(trace, PSM_2_ASM_URI_POSTFIX, modelUri, uriHandler);
    }
}
