package hu.blackbelt.judo.tatami.jql2expression;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class JqlExtract2Expression {

    public static void executeJqlExtract2ExpressionTransformation(ResourceSet resourceSet, AsmModel asmModel, PsmJqlExtractModel jqlExtractModel, ExpressionModel expressionModel, Log log,
                                                                  File scriptDir) throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResourceSet().getResource(asmModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("JQLEXTRACT")
                                .resource(jqlExtractModel.getResourceSet().getResource(jqlExtractModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("EXPR")
                                .resource(expressionModel.getResourceSet().getResource(expressionModel.getUri(), false))
                                .build()))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("jqlToExpression.etl")
                        .parameters(ImmutableList.of())
                        .build());


        executionContext.commit();
        executionContext.close();


    }
}
