package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Optional;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseIncremental.executeRdbms2LiquibaseIncrementalTransformation;

@Slf4j
public class Rdbms2LiquibaseIncrementalWork extends AbstractTransformationWork {

    final URI transformationScriptRoot;

    private final String dialect;

    public Rdbms2LiquibaseIncrementalWork(TransformationContext transformationContext, URI transformationScriptRoot, String dialect) {
        super(transformationContext);
        this.transformationScriptRoot = transformationScriptRoot;
        this.dialect = dialect;
    }

    public Rdbms2LiquibaseIncrementalWork(TransformationContext transformationContext, String dialect) {
        this(transformationContext, Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }

    @Override
    public void execute() throws Exception {
        final RdbmsModel incrementalRdbmsModel = getTransformationContext()
                .get(RdbmsModel.class, "rdbms-incremental:" + dialect)
                .orElseThrow(() -> new RuntimeException("Required rdbms-incremental:" + dialect + " cannot be found in transformation context"));

        executeRdbms2LiquibaseIncrementalTransformation(
                incrementalRdbmsModel,
                getLiquibaseModel("liquibase-beforeIncremental:" + dialect, "BeforeIncremental"),
                getLiquibaseModel("liquibase-afterIncremental:" + dialect, "AfterIncremental"),
                getLiquibaseModel("liquibase-incremental:" + dialect, "Incremental"),
                (Log) getTransformationContext().get(Log.class).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot,
                dialect);
    }

    private LiquibaseModel getLiquibaseModel(String key, String modelName) {
        final Optional<LiquibaseModel> optionalLiquibaseModel =
                getTransformationContext().get(LiquibaseModel.class, key);
        final LiquibaseModel liquibaseModel;
        if (optionalLiquibaseModel.isPresent()) {
            liquibaseModel = optionalLiquibaseModel.get();
        } else {
            liquibaseModel = buildLiquibaseModel().name(modelName).build();
            getTransformationContext().put(key, liquibaseModel);
        }
        return liquibaseModel;
    }

}
