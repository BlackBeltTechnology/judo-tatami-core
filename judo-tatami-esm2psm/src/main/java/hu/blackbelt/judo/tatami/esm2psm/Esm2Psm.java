package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.getTransformationTraceFromEtlExecutionContext;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace.ESM_2_PSM_URI_POSTFIX;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace.resolveEsm2PsmTrace;

@Slf4j
public class Esm2Psm {

    public static final String SCRIPT_ROOT_TATAMI_ESM_2_PSM = "tatami/esm2psm/transformations/psm/";
    public static final String MODEL_ROOT_TATAMI_ESM_2_PSM = "tatami/esm2psm/model/";

    /**
     * Execute ESM to PSM model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param psmModel  The PSM model definition transformed to
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2PsmTransformationTrace executeEsm2PsmTransformation(EsmModel esmModel, PsmModel psmModel) throws Exception {
        return executeEsm2PsmTransformation(esmModel, psmModel, new Slf4jLog(log), calculateEsm2PsmTransformationScriptURI());
    }

    /**
     * Execute ESM to PSM model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param psmModel  The PSM model definition transformed to
     * @param log       The log instance used in scripts
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2PsmTransformationTrace executeEsm2PsmTransformation(EsmModel esmModel, PsmModel psmModel, Log log) throws Exception {
        return executeEsm2PsmTransformation(esmModel, psmModel, log, calculateEsm2PsmTransformationScriptURI());
    }

    /**
     * Execute ESM to PSM model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param psmModel  The PSM model definition transformed to
     * @param log       The log instance used in scripts
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2PsmTransformationTrace executeEsm2PsmTransformation(EsmModel esmModel, PsmModel psmModel, Log log,
                                                                          URI scriptDir) throws Exception {
        return executeEsm2PsmTransformation(esmModel, psmModel, log, scriptDir, calculateEsm2PsmModelURI());
    }

    /**
     * Execute ESM to PSM model transformation,
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param psmModel  The PSM model definition transformed to
     * @param log       The log instance used in scripts
     * @param scriptDir The physical filesystem directory where the script root is
     * @param excelModelUri The physical filesystem directory where the Excel model for claims mapping is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Esm2PsmTransformationTrace executeEsm2PsmTransformation(EsmModel esmModel, PsmModel psmModel, Log log,
                                                                          URI scriptDir, URI excelModelUri) throws Exception {

        EsmUtils esmUtils = new EsmUtils();
        //esmUtils.processAllEntities();
        //esmUtils.processAllMixins();

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
                                .build(),
                        excelModelContextBuilder()
                                .name("CLAIMMAPPING")
                                .excel(UriUtil.resolve("PSM_claims.xlsx", excelModelUri).toString())
                                .excelConfiguration(UriUtil.resolve("claimmapping.xml", excelModelUri).toString())
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of(
                        "esmUtils", new EsmUtils(),
                        "psmUtils", new PsmUtils()
                ))
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

    public static URI calculateEsm2PsmTransformationScriptURI() throws URISyntaxException {
        return calculateURI(SCRIPT_ROOT_TATAMI_ESM_2_PSM);
    }

    public static URI calculateEsm2PsmModelURI() throws URISyntaxException {
        return calculateURI(MODEL_ROOT_TATAMI_ESM_2_PSM);
    }

    public static URI calculateURI(String path) throws URISyntaxException {
        URI psmRoot = Esm2Psm.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + path);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + path);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + path);
        }
        return psmRoot;
    }
}
