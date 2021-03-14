package hu.blackbelt.judo.tatami.esm2ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.ui.runtime.UiUtils;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
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
import static hu.blackbelt.judo.tatami.esm2ui.Esm2UiTransformationTrace.ESM_2_UI_URI_POSTFIX;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2UiTransformationTrace.resolveEsm2UiTrace;

@Slf4j
public class Esm2Ui {

    public static final String SCRIPT_ROOT_TATAMI_ESM_2_UI = "tatami/esm2ui/transformations/ui/";

    /**
     * Execute ESM to UI model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param uiModel  The UI model definition transformed to
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2UiTransformationTrace executeEsm2UiTransformation(EsmModel esmModel, String applicationType, Integer applicationColumns,
            boolean allRowActions, UiModel uiModel) throws Exception {
    	return executeEsm2UiTransformation(esmModel, applicationType, applicationColumns, allRowActions, uiModel, new Slf4jLog(log), calculateEsm2UiTransformationScriptURI());
    }

    /**
     * Execute ESM to UI model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param uiModel  The UI model definition transformed to
     * @param log       The log instance used in scripts
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2UiTransformationTrace executeEsm2UiTransformation(EsmModel esmModel, String applicationType, Integer applicationColumns,
            boolean allRowActions, UiModel uiModel, Log log) throws Exception {
    	return executeEsm2UiTransformation(esmModel, applicationType, applicationColumns, allRowActions, uiModel, log, calculateEsm2UiTransformationScriptURI());
    }

    /**
     * Execute ESM to UI model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param uiModel  The UI model definition transformed to
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2UiTransformationTrace executeEsm2UiTransformation(EsmModel esmModel, String applicationType, Integer applicationColumns,
            boolean allRowActions, UiModel uiModel, URI scriptDir) throws Exception {
    	return executeEsm2UiTransformation(esmModel, applicationType, applicationColumns, allRowActions, uiModel, new Slf4jLog(log), scriptDir);
    }

    /**
     * Execute ESM to UI model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param uiModel  The UI model definition transformed to
     * @param log       The log instance used in scripts
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2UiTransformationTrace executeEsm2UiTransformation(EsmModel esmModel, String applicationType, Integer applicationColumns,
            boolean allRowActions, UiModel uiModel, Log log, URI scriptDir) throws Exception {

        EsmUtils esmUtils = new EsmUtils();

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
                                .name("UI")
                                .resource(uiModel.getResource())
                                .build()))
                .injectContexts(ImmutableMap.of(
                        "esmUtils", new EsmUtils(),
                        "uiUtils", new UiUtils(),
                        "applicationType", applicationType,
                        "applicationColumns", applicationColumns,
                        "allRowActions", allRowActions
                ))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("esmToUi.etl", scriptDir))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);
        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(ESM_2_UI_URI_POSTFIX, etlExecutionContext);
        return Esm2UiTransformationTrace.esm2UiTransformationTraceBuilder()
                .esmModel(esmModel)
                .uiModel(uiModel)
                .trace(resolveEsm2UiTrace(traceModel, esmModel, uiModel)).build();
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateEsm2UiTransformationScriptURI() {
        URI uiRoot = Esm2Ui.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (uiRoot.toString().endsWith(".jar")) {
            uiRoot = new URI("jar:" + uiRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_ESM_2_UI);
        } else if (uiRoot.toString().startsWith("jar:bundle:")) {
            uiRoot = new URI(uiRoot.toString().substring(4, uiRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_ESM_2_UI);
        } else {
            uiRoot = new URI(uiRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_ESM_2_UI);
        }
        return uiRoot;
    }

}
