package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Esm2Psm {

    public static final String ESM_2_PSM_URI_POSTFIX = "esm2psm";

    /**
     * Execute ESM to PSM model transformation,
     * @param resourceSet {@link ResourceSet} the Epsilon transformation resource context.
     * @param esmModel The ESM model definition and loaded resources
     * @param psmModel The PSM model definition and loaded resources
     * @param log The log instance used in scripts
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2PsmTransformationTrace executeEsm2PsmTransformation(ResourceSet resourceSet, EsmModel esmModel, PsmModel psmModel, Log log,
                                                                          URI scriptDir) throws Exception {

        // If resource not created for target model
        Resource psmResource = psmModel.getResourceSet().getResource(psmModel.getUri(), false);
        if (psmResource == null) {
            psmResource = resourceSet.createResource(psmModel.getUri());
        }

        EsmUtils esmUtils = new EsmUtils(esmModel.getResourceSet(), false);
        esmUtils.processAllEntities();
        esmUtils.processAllMixins();

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ESM")
                                .resource(esmModel.getResourceSet().getResource(esmModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("JUDOPSM")
                                .resource(psmResource)
                                .build()))
                .injectContexts(ImmutableMap.of("esmUtils", esmUtils))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source(scriptDir.resolve("esmToPsm.etl"))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);
        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTrace(ESM_2_PSM_URI_POSTFIX, etlExecutionContext);
        return Esm2PsmTransformationTrace.esm2PsmTransformationTraceBuilder()
                .esmModel(esmModel)
                .psmModel(psmModel)
                .trace(resolveEsm2PsmTrace(traceModel, esmModel, psmModel)).build();
    }

    public static ResourceSet createEsm2PsmTraceResourceSet() {
        return createTraceResourceSet(ESM_2_PSM_URI_POSTFIX);
    }

    public static Map<EObject, List<EObject>> resolveEsm2PsmTrace(Resource traceResource, EsmModel esmModel, PsmModel psmModel) {
        return resolveEsm2PsmTrace(traceResource.getContents(), esmModel, psmModel);
    }

    public static Map<EObject, List<EObject>> resolveEsm2PsmTrace(List<EObject> trace, EsmModel esmModel, PsmModel psmModel) {
        return resolveTransformationTrace(trace,
                ImmutableList.of(esmModel.getResourceSet(), psmModel.getResourceSet()));
    }

    public static List<EObject> getEsm2PsmTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTrace(ESM_2_PSM_URI_POSTFIX, trace);
    }
}
