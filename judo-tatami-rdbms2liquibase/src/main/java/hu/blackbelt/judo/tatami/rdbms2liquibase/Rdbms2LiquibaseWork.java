package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.buildLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;

@Slf4j
public class Rdbms2LiquibaseWork extends AbstractTransformationWork {

    final URI transformationScriptRoot;

    private final String dialect;

    public Rdbms2LiquibaseWork(TransformationContext transformationContext, URI transformationScriptRoot, String dialect) {
        super(transformationContext);
        this.transformationScriptRoot = transformationScriptRoot;
        this.dialect = dialect;
    }

    public Rdbms2LiquibaseWork(TransformationContext transformationContext, String dialect) {
        this(transformationContext, Rdbms2Liquibase.calculateRdbms2LiquibaseTransformationScriptURI(), dialect);
    }

    @Override
    public void execute() throws Exception {
        final RdbmsModel rdbmsModel = getTransformationContext()
                .get(RdbmsModel.class, "rdbms:" + dialect)
                .orElseThrow(() -> new IllegalArgumentException("RDBMS Model of " + dialect + " dialect does not found in transformation context"));

        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());

        final LiquibaseModel liquibaseModel = getTransformationContext()
                .getByClass(LiquibaseModel.class)
                .orElseGet(() -> buildLiquibaseModel().name(rdbmsModel.getName()).build());
        getTransformationContext().put("liquibase:" + dialect, liquibaseModel);

        Rdbms2Liquibase.executeRdbms2LiquibaseTransformation(
                rdbmsModel,
                liquibaseModel,
                (Log) getTransformationContext().get(Log.class).orElseGet(() -> new Slf4jLog(log)),
                transformationScriptRoot,
                dialect);
    }

}
