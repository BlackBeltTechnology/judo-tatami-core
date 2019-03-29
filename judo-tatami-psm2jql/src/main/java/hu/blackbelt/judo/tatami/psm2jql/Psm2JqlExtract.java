package hu.blackbelt.judo.tatami.psm2jql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Psm2JqlExtract {

    public static void executePsm2PsmJqlExtractTransformation(ResourceSet resourceSet, PsmModel psmModel, PsmJqlExtractModel jqlExtractModel, Log log,
                                                    File scriptDir) throws Exception {

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
                                .resource(jqlExtractModel.getResourceSet().getResource(jqlExtractModel.getUri(), false))
                                .build()))
                .injectContexts(ImmutableMap.of(
                        "jqlParser", new JqlParser()
                ))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("psmToJql.etl")
                        .parameters(ImmutableList.of(
                                programParameterBuilder().name("modelName").value(psmModel.getName()).build()
                        ))
                        .build());


        executionContext.commit();
        executionContext.close();


    }
}
