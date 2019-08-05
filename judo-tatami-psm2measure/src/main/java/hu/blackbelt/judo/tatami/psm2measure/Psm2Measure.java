package hu.blackbelt.judo.tatami.psm2measure;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Psm2Measure {

    public static final String PSM_2_MEASURE_URI_POSTFIX = "psm2measure";

    public static Psm2MeasureTransformationTrace executePsm2MeasureTransformation(PsmModel psmModel, MeasureModel measureModel, Log log,
                                                                                  URI scriptUri) throws Exception {
        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(measureModel.getResourceSet())
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("JUDOPSM")
                                .resource(psmModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("MEASURES")
                                .resource(measureModel.getResource())
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext =
                etlExecutionContextBuilder()
                        .source(UriUtil.resolve("psmToMeasure.etl", scriptUri))
                        .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(PSM_2_MEASURE_URI_POSTFIX, etlExecutionContext);
        return Psm2MeasureTransformationTrace.psm2MeasureTransformationTraceBuilder()
                .measureModel(measureModel)
                .psmModel(psmModel)
                .trace(resolvePsm2MeasureTrace(traceModel, psmModel, measureModel)).build();
    }


    public static ResourceSet createPsm2MeasureTraceResourceSet() {
        return createTraceResourceSet(PSM_2_MEASURE_URI_POSTFIX);
    }

    public static Map<EObject, List<EObject>> resolvePsm2MeasureTrace(Resource traceResource, PsmModel psmModel, MeasureModel measureModel) {
        return resolvePsm2MeasureTrace(traceResource.getContents(), psmModel, measureModel);
    }

    public static Map<EObject, List<EObject>> resolvePsm2MeasureTrace(List<EObject> trace, PsmModel psmModel, MeasureModel measureModel) {
        return resolveTransformationTraceAsEObjectMap(trace,
                ImmutableList.of(psmModel.getResourceSet(), measureModel.getResourceSet()));
    }

    public static List<EObject> getPsm2MeasureTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTraceFromEtlExecutionContext(PSM_2_MEASURE_URI_POSTFIX, trace);
    }

}
