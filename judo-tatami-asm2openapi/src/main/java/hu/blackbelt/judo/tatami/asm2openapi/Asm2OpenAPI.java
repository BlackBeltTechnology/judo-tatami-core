package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Asm2OpenAPI {

    public static void executeAsm2OpenAPITransformation(ResourceSet resourceSet, AsmModel asmModel, OpenAPIModel openAPIModel, Log log,
                                                      File scriptDir) throws Exception {


        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("OPENAPI")
                                .resource(openAPIModel.getResource())
                                .build()
                        )
                )
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("asmToOpenAPI.etl")
                        .parameters(ImmutableList.of(
                                programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                                programParameterBuilder().name("extendedMetadataURI")
                                        .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                        ))
                        .build());

        executionContext.commit();
        executionContext.close();


    }
}
