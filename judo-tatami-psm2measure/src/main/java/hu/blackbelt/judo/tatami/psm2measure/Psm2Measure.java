package hu.blackbelt.judo.tatami.psm2measure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.getTransformationTraceFromEtlExecutionContext;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace.PSM_2_MEASURE_URI_POSTFIX;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace.resolvePsm2MeasureTrace;

@Slf4j
public class Psm2Measure {

    public static final String SCRIPT_ROOT_TATAMI_PSM_2_MEASURE = "tatami/psm2measure/transformations/measure/";

    public static Psm2MeasureTransformationTrace executePsm2MeasureTransformation(PsmModel psmModel, MeasureModel measureModel) throws Exception {
        return executePsm2MeasureTransformation(psmModel, measureModel, new Slf4jLog(log), calculatePsm2MeasureTransformationScriptURI());
    }

    public static Psm2MeasureTransformationTrace executePsm2MeasureTransformation(PsmModel psmModel, MeasureModel measureModel, Log log) throws Exception {
        return executePsm2MeasureTransformation(psmModel, measureModel, log, calculatePsm2MeasureTransformationScriptURI());
    }

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
                .injectContexts(ImmutableMap.of(
                        "psmUtils", new PsmUtils()))
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

    @SneakyThrows(URISyntaxException.class)
    public static URI calculatePsm2MeasureTransformationScriptURI() {
        URI psmRoot = Psm2Measure.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_PSM_2_MEASURE);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_PSM_2_MEASURE);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_PSM_2_MEASURE);
        }
        return psmRoot;
    }

}
