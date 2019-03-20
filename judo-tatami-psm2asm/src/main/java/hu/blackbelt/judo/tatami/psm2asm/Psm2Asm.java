package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Psm2Asm {

    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";

    public static void executePsm2AsmTransformation(ResourceSet resourceSet, PsmModel psmModel, AsmModel asmModel, Log log,
                                             File scriptDir) throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
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
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("psmToAsm.etl")
                        .parameters(ImmutableList.of(
                                programParameterBuilder().name("modelName").value(psmModel.getName()).build(),
                                programParameterBuilder().name("nsURI").value("http://blackbelt.hu/judo/" + psmModel.getName()).build(),
                                programParameterBuilder().name("nsPrefix").value("runtime" + psmModel.getName()).build(),
                                programParameterBuilder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                        ))
                        .build());


        executionContext.commit();
        executionContext.close();


    }
}