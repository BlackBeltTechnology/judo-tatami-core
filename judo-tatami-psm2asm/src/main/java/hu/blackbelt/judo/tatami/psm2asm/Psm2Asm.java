package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
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
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace.resolvePsm2AsmTrace;

@Slf4j
public class Psm2Asm {

    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    public static final String SCRIPT_ROOT_TATAMI_PSM_2_ASM = "tatami/psm2asm/transformations/asm/";

    /**
     * Execute PSM to ASM model transformation,
     * @param psmModel The PSM model definition and loaded resources
     * @param asmModel The asm model definition (target)
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Psm2AsmTransformationTrace executePsm2AsmTransformation(PsmModel psmModel, AsmModel asmModel) throws Exception {
        return executePsm2AsmTransformation(psmModel, asmModel, new Slf4jLog(log), calculatePsm2AsmTransformationScriptURI());
    }

    /**
     * Execute PSM to ASM model transformation,
     * @param psmModel The PSM model definition and loaded resources
     * @param asmModel The asm model definition (target)
     * @param log The log instance used in scripts
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Psm2AsmTransformationTrace executePsm2AsmTransformation(PsmModel psmModel, AsmModel asmModel, Log log) throws Exception {
        return executePsm2AsmTransformation(psmModel, asmModel, log, calculatePsm2AsmTransformationScriptURI());
    }

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

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(
                Psm2AsmTransformationTrace.PSM_2_ASM_URI_POSTFIX, etlExecutionContext);
        return Psm2AsmTransformationTrace.psm2AsmTransformationTraceBuilder()
                .asmModel(asmModel)
                .psmModel(psmModel)
                .trace(resolvePsm2AsmTrace(traceModel, psmModel, asmModel)).build();
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculatePsm2AsmTransformationScriptURI() {
        URI psmRoot = Psm2Asm.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_PSM_2_ASM);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_PSM_2_ASM);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_PSM_2_ASM);
        }
        return psmRoot;
    }

}
