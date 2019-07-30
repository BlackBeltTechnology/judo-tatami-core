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

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Psm2Measure {

    public static final String PSM_2_MEASURE_URI_POSTFIX = "psm2measure";

    public static Psm2MeasureTransformationTrace executePsm2MeasureTransformation(ResourceSet resourceSet, PsmModel psmModel, MeasureModel measureModel, Log log,
                                                                                  URI scriptUri) throws Exception {

        // If resource not creared for target model
        Resource measureResource = measureModel.getResourceSet().getResource(measureModel.getUri(), false);
        if (measureResource == null) {
            measureResource = resourceSet.createResource(measureModel.getUri());
        }

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("JUDOPSM")
                                .resource(psmModel.getResourceSet().getResource(psmModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("MEASURES")
                                .resource(measureResource)
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext =
                etlExecutionContextBuilder()
                        .source(scriptUri.resolve("psmToMeasure.etl"))
                        .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTrace(PSM_2_MEASURE_URI_POSTFIX, etlExecutionContext);
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
        return resolveTransformationTrace(trace,
                ImmutableList.of(psmModel.getResourceSet(), measureModel.getResourceSet()));
    }

    public static List<EObject> getPsm2MeasureTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTrace(PSM_2_MEASURE_URI_POSTFIX, trace);
    }

}
