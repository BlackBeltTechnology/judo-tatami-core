package hu.blackbelt.judo.tatami.psm2jql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
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

public class Psm2JqlExtract {

    public static final String PSM_2_JQLEXTRACT_URI_POSTFIX = "psm2jqlextract";

    public static Psm2JqlExtractTrackInfo executePsm2PsmJqlExtractTransformation(ResourceSet resourceSet, PsmModel psmModel, PsmJqlExtractModel jqlExtractModel, Log log,
                                                    File scriptDir) throws Exception {


        // If resource not creared for target model
        Resource jqlExtractResource = jqlExtractModel.getResourceSet().getResource(jqlExtractModel.getUri(), false);
        if (jqlExtractResource == null) {
            jqlExtractResource = resourceSet.createResource(jqlExtractModel.getUri());
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
                                .name("JQLEXTRACT")
                                .resource(jqlExtractResource)
                                .build()))
                .injectContexts(ImmutableMap.of(
                        "jqlParser", new JqlParser()
                ))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext etlExecutionContext =  etlExecutionContextBuilder()
                .source("psmToJql.etl")
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelName").value(psmModel.getName()).build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);


        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTrace(PSM_2_JQLEXTRACT_URI_POSTFIX, etlExecutionContext);
        return Psm2JqlExtractTrackInfo.psm2JqlExtractTrackInfoBuilder()
                .psmJqlExtractModel(jqlExtractModel)
                .psmModel(psmModel)
                .trace(resolvePsm2JqlExtractTrace(traceModel, psmModel, jqlExtractModel)).build();
    }

    public static ResourceSet createPsm2JqlExtractTraceResourceSet() {
        return createTraceResourceSet(PSM_2_JQLEXTRACT_URI_POSTFIX);
    }

    public static Map<EObject, List<EObject>> resolvePsm2JqlExtractTrace(Resource traceResource, PsmModel psmModel, PsmJqlExtractModel psmJqlExtractModel) {
        return resolvePsm2JqlExtractTrace(traceResource.getContents(), psmModel, psmJqlExtractModel);
    }

    public static Map<EObject, List<EObject>> resolvePsm2JqlExtractTrace(List<EObject> trace, PsmModel psmModel, PsmJqlExtractModel psmJqlExtractModel) {
        return resolveTransformationTrace(trace,
                ImmutableList.of(psmModel.getResourceSet(), psmJqlExtractModel.getResourceSet()));
    }

    public static List<EObject> getPsm2JqlExtractTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTrace(PSM_2_JQLEXTRACT_URI_POSTFIX, trace);
    }

}
