package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Rdbms2Liquibase {

    public static void executeRdbms2LiquibaseTransformation(RdbmsModel rdbmsModel, hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel liquibaseModel, Log log,
                                                            URI scriptUri, String dialect) throws Exception {

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(liquibaseModel.getResourceSet())
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("RDBMS")
                                .resource(rdbmsModel.getResourceSet().getResource(rdbmsModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("LIQUIBASE")
                                .resource(liquibaseModel.getResource())
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(UriUtil.resolve("rdbmsToLiquibase.etl", scriptUri))
                        .parameters(ImmutableList.of(
                                ProgramParameter.programParameterBuilder().name("dialect").value(dialect).build()
                        ))
                        .build());

        executionContext.commit();
        executionContext.close();
    }
}
