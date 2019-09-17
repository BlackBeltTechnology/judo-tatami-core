package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.util.List;
import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace.ESM_2_PSM_URI_POSTFIX;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace.resolveEsm2PsmTrace;

public class Esm2Psm {

    /**
     * Execute ESM to PSM model transformation,
     * @param esmModel The ESM model definition and loaded resources
     * @param psmModel The PSM model definition transformed to
     * @param log The log instance used in scripts
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2PsmTransformationTrace executeEsm2PsmTransformation(EsmModel esmModel, PsmModel psmModel, Log log,
                                                                          URI scriptDir) throws Exception {

        EsmUtils esmUtils = new EsmUtils(esmModel.getResourceSet(), false);
        esmUtils.processAllEntities();
        esmUtils.processAllMixins();

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ESM")
                                .resource(esmModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("JUDOPSM")
                                .resource(psmModel.getResource())
                                .build()))
                .injectContexts(ImmutableMap.of("esmUtils", esmUtils))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("esmToPsm.etl", scriptDir))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);
        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(ESM_2_PSM_URI_POSTFIX, etlExecutionContext);
        return Esm2PsmTransformationTrace.esm2PsmTransformationTraceBuilder()
                .esmModel(esmModel)
                .psmModel(psmModel)
                .trace(resolveEsm2PsmTrace(traceModel, esmModel, psmModel)).build();
    }
}
