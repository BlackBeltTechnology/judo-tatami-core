package hu.blackbelt.judo.tatami.jql2expression;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class JqlExtract2Expression {

    public static final String JQLEXTRACT_2_EXPRESSION_URI_POSTFIX = "jqlextract2expression";

    public static JqlExtract2ExpressionTrackInfo executeJqlExtract2ExpressionTransformation(ResourceSet resourceSet, AsmModel asmModel, PsmJqlExtractModel jqlExtractModel, ExpressionModel expressionModel, Log log,
                                                                  File scriptDir) throws Exception {


        // If resource was not created for target model before
        Resource expressionResource = expressionModel.getResourceSet().getResource(expressionModel.getUri(), false);
        if (expressionResource == null) {
            expressionResource = resourceSet.createResource(expressionModel.getUri());
        }

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
                                .resource(expressionResource)
                                .build()))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();


        EtlExecutionContext etlExecutionContext =
                etlExecutionContextBuilder()
                        .source("jqlToExpression.etl")
                        .parameters(ImmutableList.of())
                        .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);


        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTrace(JQLEXTRACT_2_EXPRESSION_URI_POSTFIX, etlExecutionContext);

        return JqlExtract2ExpressionTrackInfo.jqlExtract2ExpressionTrackInfoBuilder()
                .psmJqlExtractModel(jqlExtractModel)
                .expressionModel(expressionModel)
                .trace(resolveJqlExtract2ExpressionTrace(traceModel, jqlExtractModel, expressionModel)).build();
    }


    public static ResourceSet createJqlExtract2ExpressionTraceResourceSet() {
        return createTraceResourceSet(JQLEXTRACT_2_EXPRESSION_URI_POSTFIX);
    }

    public static Map<EObject, List<EObject>> resolveJqlExtract2ExpressionTrace(Resource traceResource, PsmJqlExtractModel psmJqlExtractModel, ExpressionModel expressionModel) {
        return resolveJqlExtract2ExpressionTrace(traceResource.getContents(), psmJqlExtractModel, expressionModel);
    }

    public static Map<EObject, List<EObject>> resolveJqlExtract2ExpressionTrace(List<EObject> trace, PsmJqlExtractModel psmJqlExtractModel, ExpressionModel expressionModel) {
        return resolveTransformationTrace(trace,
                ImmutableList.of(psmJqlExtractModel.getResourceSet(), expressionModel.getResourceSet()));
    }

    public static List<EObject> getJqlExtract2ExpressionTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTrace(JQLEXTRACT_2_EXPRESSION_URI_POSTFIX, trace);
    }
}
