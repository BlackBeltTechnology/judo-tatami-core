package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

@Slf4j
public class Rdbms2Liquibase {

    public static final String SCRIPT_ROOT_TATAMI_RDBMS_2_LIQUIBASE = "tatami/rdbms2liquibase/transformations/";

    public static void executeRdbms2LiquibaseTransformation(RdbmsModel rdbmsModel, hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel liquibaseModel, String dialect) throws Exception {
        executeRdbms2LiquibaseTransformation(rdbmsModel, liquibaseModel, new Slf4jLog(log), calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }

    public static void executeRdbms2LiquibaseTransformation(RdbmsModel rdbmsModel, hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel liquibaseModel, Log log, String dialect) throws Exception {
        executeRdbms2LiquibaseTransformation(rdbmsModel, liquibaseModel, log, calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }


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

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateRdbms2LiquibaseTransformationScriptURI() {
        URI rdbmsRoot = Rdbms2Liquibase.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (rdbmsRoot.toString().endsWith(".jar")) {
            rdbmsRoot = new URI("jar:" + rdbmsRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_RDBMS_2_LIQUIBASE);
        } else if (rdbmsRoot.toString().startsWith("jar:bundle:")) {
            rdbmsRoot = new URI(rdbmsRoot.toString().substring(4, rdbmsRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_RDBMS_2_LIQUIBASE);
        } else {
            rdbmsRoot = new URI(rdbmsRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_RDBMS_2_LIQUIBASE);
        }
        return rdbmsRoot;
    }

}
