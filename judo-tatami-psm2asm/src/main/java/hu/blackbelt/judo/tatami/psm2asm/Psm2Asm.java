package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Psm2Asm {

    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    public static final String PSM_2_ASM_URI_POSTFIX = "psm2asm";

    /**
     * Execute PSM to ASM model transformation,
     * @param resourceSet {@link ResourceSet} the Epsilon transformation resource context.
     * @param psmModel The PSM model definition and loaded resources
     * @param asmModel The asm model definition and loaded resources
     * @param log The log instance used in scripts
     * @param scriptDir The physucal filesystem directory where the script root is
     * @return The trace object list of the transformation conforms the meta model defined in {@link TransformationTraceUtil}.
     * @throws Exception
     */
    public static Psm2AsmTrackInfo executePsm2AsmTransformation(ResourceSet resourceSet, PsmModel psmModel, AsmModel asmModel, Log log,
                                                                   File scriptDir) throws Exception {

        // If resource not creared for target model
        Resource asmResource = asmModel.getResourceSet().getResource(asmModel.getUri(), false);
        if (asmResource == null) {
            asmResource = resourceSet.createResource(asmModel.getUri());
        }

        // Executrion context
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
                                .name("ASM")
                                .resource(asmResource)
                                .build()))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source("psmToAsm.etl")
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

        List<EObject> traceModel = getTransformationTrace(PSM_2_ASM_URI_POSTFIX, etlExecutionContext);
        return Psm2AsmTrackInfo.psm2AsmTrackInfoBuilder()
                .asmModel(asmModel)
                .psmModel(psmModel)
                .trace(resolvePsm2AsmTrace(traceModel, psmModel, asmModel)).build();
    }

    public static ResourceSet createPsm2AsmTraceResourceSet() {
        return createTraceResourceSet(PSM_2_ASM_URI_POSTFIX);
    }

    public static Map<EObject, List<EObject>> resolvePsm2AsmTrace(Resource traceResource, PsmModel psmModel, AsmModel asmModel) {
        return resolvePsm2AsmTrace(traceResource.getContents(), psmModel, asmModel);
    }

    public static Map<EObject, List<EObject>> resolvePsm2AsmTrace(List<EObject> trace, PsmModel psmModel, AsmModel asmModel) {
        return resolveTransformationTrace(trace,
                ImmutableList.of(psmModel.getResourceSet(), asmModel.getResourceSet()));
    }

    public static List<EObject> getPsm2AsmTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTrace(PSM_2_ASM_URI_POSTFIX, trace);
    }
}
