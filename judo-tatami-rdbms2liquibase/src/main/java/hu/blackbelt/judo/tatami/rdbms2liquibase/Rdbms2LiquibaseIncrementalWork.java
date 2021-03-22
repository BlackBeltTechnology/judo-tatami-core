package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2LiquibaseIncremental.executeRdbms2LiquibaseIncrementalTransformation;
import static java.nio.file.Files.createTempDirectory;

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

        Optional<String> sqlOutputOptional = getTransformationContext().get(String.class, "liquibase-incremental:" + dialect + "-sqlOutput");
        if (!sqlOutputOptional.isPresent()) {
            final File tempDir = createTempDirectory("liquibase-sql-" + dialect).toFile();
            tempDir.deleteOnExit();
            getTransformationContext().put("liquibase-incremental:" + dialect + "-sqlOutput", tempDir.getAbsolutePath());
            sqlOutputOptional = Optional.of(tempDir.getAbsolutePath());
        }

        Optional<String> sqlScriptOptional = getTransformationContext().get(String.class, "liquibase-incremental:" + dialect + "-sqlScriptPath");
        if (!sqlScriptOptional.isPresent()) {
            final File tempDir = createTempDirectory("liquibase-sql-script-" + dialect).toFile();
            tempDir.deleteOnExit();
            getTransformationContext().put("liquibase-incremental:" + dialect + "-sqlScriptPath", tempDir.getAbsolutePath());
            sqlScriptOptional = Optional.of(tempDir.getAbsolutePath());
        }

        executeRdbms2LiquibaseIncrementalTransformation(
                incrementalRdbmsModel,
                getLiquibaseModel("liquibase-dbCheckup:" + dialect, "DbCheckup"),
                getLiquibaseModel("liquibase-dbBackup:" + dialect, "DbBackup"),
                getLiquibaseModel("liquibase-beforeIncremental:" + dialect, "BeforeIncremental"),
                getLiquibaseModel("liquibase-updateDataBeforeIncremental:" + dialect, "UpdateDataBeforeIncremental"),
                getLiquibaseModel("liquibase-incremental:" + dialect, "Incremental"),
                getLiquibaseModel("liquibase-updateDataAfterIncremental:" + dialect, "UpdateDataAfterIncremental"),
                getLiquibaseModel("liquibase-afterIncremental:" + dialect, "AfterIncremental"),
                getLiquibaseModel("liquibase-dbDropBackup:" + dialect, "DbDropBackup"),
                (Log) getTransformationContext().get(Log.class).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot,
                dialect,
                sqlOutputOptional.get(),
                sqlScriptOptional.get());
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
