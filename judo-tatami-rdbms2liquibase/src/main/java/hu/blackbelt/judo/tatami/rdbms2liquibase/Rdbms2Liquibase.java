package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Rdbms2Liquibase {

    public static void executeRdbms2LiquibaseTransformation(ResourceSet resourceSet, RdbmsModel rdbmsModel, hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel liquibaseModel, Log log,
                                                            File scriptDir, String dialect) throws Exception {

        // If resource was not created for target model before
        Resource liquibaseResource = liquibaseModel.getResourceSet().getResource(liquibaseModel.getUri(), false);
        if (liquibaseResource == null) {
            liquibaseResource = resourceSet.createResource(liquibaseModel.getUri());
        }


        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("RDBMS")
                                .resource(rdbmsModel.getResourceSet().getResource(rdbmsModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("LIQUIBASE")
                                .resource(liquibaseResource)
                                .build()))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("rdbmsToLiquibase.etl")
                        .parameters(ImmutableList.of(
                                ProgramParameter.programParameterBuilder().name("dialect").value(dialect).build()
                        ))
                        .build());

        executionContext.commit();
        executionContext.close();
    }
}
