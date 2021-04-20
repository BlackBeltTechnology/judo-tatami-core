package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.util.UriUtil;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext.eglExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI;
import static java.util.Collections.singletonList;

@Slf4j
public class Rdbms2LiquibaseIncremental {

    private static final String BACKUP_PREFIX = "BACKUP";

    public static Map<String, String> executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel dbCheckupLiquibaseModel,
                                                                       LiquibaseModel dbBackupLiquibaseModel,
                                                                       LiquibaseModel beforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel updateDataBeforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       LiquibaseModel updateDataAfterIncrementalLiquibaseModel,
                                                                       LiquibaseModel afterIncrementalLiquibaseModel,
                                                                       LiquibaseModel dbDropBackupLiquibaseModel,
                                                                       String dialect,
                                                                       String sqlOutput,
                                                                       String sqlScriptPath) throws Exception {
        return executeRdbms2LiquibaseIncrementalTransformation(incrementalRdbmsModel, dbCheckupLiquibaseModel, dbBackupLiquibaseModel,
                beforeIncrementalLiquibaseModel, updateDataBeforeIncrementalLiquibaseModel, incrementalLiquibaseModel, updateDataAfterIncrementalLiquibaseModel, afterIncrementalLiquibaseModel, dbDropBackupLiquibaseModel,
                new Slf4jLog(log), calculateRdbms2LiquibaseTransformationScriptURI(), dialect, sqlOutput, sqlScriptPath);
    }

    public static Map<String, String> executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel dbCheckupLiquibaseModel,
                                                                       LiquibaseModel dbBackupLiquibaseModel,
                                                                       LiquibaseModel beforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel updateDataBeforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       LiquibaseModel updateDataAfterIncrementalLiquibaseModel,
                                                                       LiquibaseModel afterIncrementalLiquibaseModel,
                                                                       LiquibaseModel dbDropBackupLiquibaseModel,
                                                                       Log log,
                                                                       String dialect,
                                                                       String sqlOutput,
                                                                       String sqlScriptPath) throws Exception {
        return executeRdbms2LiquibaseIncrementalTransformation(incrementalRdbmsModel, dbCheckupLiquibaseModel, dbBackupLiquibaseModel,
                beforeIncrementalLiquibaseModel, updateDataBeforeIncrementalLiquibaseModel, incrementalLiquibaseModel, updateDataAfterIncrementalLiquibaseModel, afterIncrementalLiquibaseModel, dbDropBackupLiquibaseModel,
                log, calculateRdbms2LiquibaseTransformationScriptURI(), dialect, sqlOutput, sqlScriptPath);
    }

    public static Map<String, String> executeRdbms2LiquibaseIncrementalTransformation(RdbmsModel incrementalRdbmsModel,
                                                                       LiquibaseModel dbCheckupLiquibaseModel,
                                                                       LiquibaseModel dbBackupLiquibaseModel,
                                                                       LiquibaseModel beforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel updateDataBeforeIncrementalLiquibaseModel,
                                                                       LiquibaseModel incrementalLiquibaseModel,
                                                                       LiquibaseModel updateDataAfterIncrementalLiquibaseModel,
                                                                       LiquibaseModel afterIncrementalLiquibaseModel,
                                                                       LiquibaseModel dbDropBackupLiquibaseModel,
                                                                       Log log,
                                                                       URI scriptUri,
                                                                       String dialect,
                                                                       String sqlOutput,
                                                                       String sqlScriptPath) throws Exception {

        Map<String, String> missingReviewScripts = new HashMap<>();

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(incrementalRdbmsModel.getResourceSet())
                .injectContexts(ImmutableMap.of(
                        "rdbmsUtils", new RdbmsUtils(),
                        "missingReviewScripts", missingReviewScripts))
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
                                .name("DBBACKUP")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(dbBackupLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("BEFORE_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(beforeIncrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("DATA_UPDATE_BEFORE_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(updateDataBeforeIncrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("LIQUIBASE")
                                .resource(incrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("AFTER_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(afterIncrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("DATA_UPDATE_AFTER_INCREMENTAL")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(updateDataAfterIncrementalLiquibaseModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .name("DBDROPBACKUP")
                                .aliases(singletonList("LIQUIBASE"))
                                .resource(dbDropBackupLiquibaseModel.getResource())
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        final ImmutableList<ProgramParameter> parameters = ImmutableList.of(
                programParameterBuilder().name("dialect").value(dialect).build(),
                programParameterBuilder().name("backupTableNamePrefix").value(BACKUP_PREFIX).build(),
                programParameterBuilder().name("backupChangeSetNamePrefix").value(BACKUP_PREFIX.toLowerCase()).build(),
                programParameterBuilder().name("sqlOutput").value(new File(sqlOutput).getAbsolutePath()).build(),
                programParameterBuilder().name("sqlScriptPath").value(sqlScriptPath).build()
        );

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(UriUtil.resolve("rdbmsIncrementalToLiquibase.etl", scriptUri))
                        .parameters(parameters)
                        .build());

        // Generation script
        executionContext.executeProgram(
                eglExecutionContextBuilder()
                        .source(UriUtil.resolve("../generations/sql/main.egl", scriptUri))
                        .outputRoot(sqlOutput)
                        .parameters(parameters)
                        .build());

        executionContext.commit();
        executionContext.close();

        return missingReviewScripts;
    }

}
