package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;
import static java.util.Collections.singletonList;

@Slf4j
public class Rdbms2LiquibaseIncremental {

    private static final String BACKUP_PREFIX = "BACKUP";

    public static void executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel dbCheckupLiquibaseModel,
                                                                       LiquibaseModel beforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel afterIncrementalLiquibaseModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       String dialect) throws Exception {
        executeRdbms2LiquibaseIncrementalTransformation(incrementalRdbmsModel, dbCheckupLiquibaseModel, beforeIncrementalLiquibaseModel, afterIncrementalLiquibaseModel, incrementalLiquibaseModel, new Slf4jLog(log), calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }

    public static void executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel dbCheckupLiquibaseModel,
                                                                       LiquibaseModel beforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel afterIncrementalLiquibaseModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       Log log,
                                                                       String dialect) throws Exception {
        executeRdbms2LiquibaseIncrementalTransformation(incrementalRdbmsModel, dbCheckupLiquibaseModel, beforeIncrementalLiquibaseModel, afterIncrementalLiquibaseModel, incrementalLiquibaseModel, log, calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }

    public static void executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel dbCheckupLiquibaseModel,
                                                                       LiquibaseModel beforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel afterIncrementalLiquibaseModel,
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
                                .name("RDBMS")
                                .resource(incrementalRdbmsModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("DBCHECKUP")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(dbCheckupLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("BEFORE_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(beforeIncrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("AFTER_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(afterIncrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("LIQUIBASE")
                                .resource(incrementalLiquibaseModel.getResource())
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(UriUtil.resolve("rdbmsIncrementalToLiquibase.etl", scriptUri))
                        .parameters(ImmutableList.of(
                                programParameterBuilder().name("dialect").value(dialect).build(),
                                programParameterBuilder().name("backupPrefix").value(BACKUP_PREFIX).build(),
                                programParameterBuilder().name("backupPrefixLower").value(BACKUP_PREFIX.toLowerCase()).build()))
                        .build());

        executionContext.commit();
        executionContext.close();

        executeRdbms2LiquibaseTransformation(incrementalRdbmsModel, incrementalLiquibaseModel, dialect);
    }

}
