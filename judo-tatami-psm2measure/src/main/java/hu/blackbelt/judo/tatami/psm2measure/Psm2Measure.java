package hu.blackbelt.judo.tatami.psm2measure;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Psm2Measure {

    public static void executePsm2MeasureTransformation(ResourceSet resourceSet, PsmModel psmModel, MeasureModel measureModel, Log log,
                                                        File scriptDir) throws Exception {

        // Execution context
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
                                .name("MEASURES")
                                .resource(measureModel.getResourceSet().getResource(measureModel.getUri(), false))
                                .build()))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("psmToMeasure.etl")
                        .build());

        executionContext.commit();
        executionContext.close();


    }
}
