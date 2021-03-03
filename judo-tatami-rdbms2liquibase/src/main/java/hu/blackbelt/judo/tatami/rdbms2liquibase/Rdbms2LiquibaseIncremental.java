package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Slf4j
public class Rdbms2LiquibaseIncremental {

    public static final String SCRIPT_ROOT_TATAMI_RDBMS_2_LIQUIBASE = "tatami/rdbms2liquibase/transformations/";

    public static void executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel originalModel,
                                                                       RdbmsModel newModel,
                                                                       RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel beforeIncrementalModel,
                                                                       LiquibaseModel afterIncrementalModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       String dialect) throws Exception {
        executeRdbms2LiquibaseIncrementalTransformation(originalModel, newModel, incrementalRdbmsModel, beforeIncrementalModel, afterIncrementalModel, incrementalLiquibaseModel, new Slf4jLog(log), calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }

    public static void executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel originalModel,
                                                                       RdbmsModel newModel,
                                                                       RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel beforeIncrementalModel,
                                                                       LiquibaseModel afterIncrementalModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       Log log,
                                                                       String dialect) throws Exception {
        executeRdbms2LiquibaseIncrementalTransformation(originalModel, newModel, incrementalRdbmsModel, beforeIncrementalModel, afterIncrementalModel, incrementalLiquibaseModel, log, calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }


    public static void executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel originalModel,
                                                                       RdbmsModel newModel,
                                                                       RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel beforeIncrementalModel,
                                                                       LiquibaseModel afterIncrementalModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       Log log,
                                                                       URI scriptUri,
                                                                       String dialect) throws Exception {

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(incrementalRdbmsModel.getResourceSet())
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .name("PREVIOUS")
                                .aliases(asList("SOURCE", "RDBMS"))
                                .resource(originalModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("NEW")
                                .aliases(asList("SOURCE", "RDBMS"))
                                .resource(newModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("INCREMENTAL")
                                .resource(incrementalRdbmsModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("BEFORE_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(beforeIncrementalModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("AFTER_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(afterIncrementalModel.getResource())
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(UriUtil.resolve("createIncrementalOperationModel.etl", scriptUri))
                        .parameters(ImmutableList.of(
                                ProgramParameter.programParameterBuilder().name("dialect").value(dialect).build()
                        ))
                        .build());

        executionContext.commit();
        executionContext.close();

        executeRdbms2LiquibaseTransformation(incrementalRdbmsModel, incrementalLiquibaseModel, dialect);
    }

}
