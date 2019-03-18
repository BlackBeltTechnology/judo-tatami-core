package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.createLiquibaseResourceSet;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.loadLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.registerRdbmsMetamodel;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;


@Component(immediate = true, service = Rdbms2LiquibaseSerivce.class)
@Slf4j
public class Rdbms2LiquibaseSerivce {

    public static final String LIQUIBASE_META_VERSION_RANGE = "Liquibase-Meta-Version-Range";

    @Reference
    Rdbms2LiquibaseScriptResource rdbms2LiquibaseScriptResource;


    public LiquibaseModel install(RdbmsModel rdbmsModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createLiquibaseResourceSet(bundleURIHandler);

        LiquibaseModel liquibaseModel = loadLiquibaseModel(resourceSet, URI.createURI("urn:" + rdbmsModel.getName()),
                rdbmsModel.getName(), rdbmsModel.getVersion(), rdbmsModel.getChecksum(),
                bundleContext.getBundle().getHeaders().get(LIQUIBASE_META_VERSION_RANGE));

        registerRdbmsMetamodel(resourceSet);

        executeRdbms2LiquibaseTransformation(resourceSet, rdbmsModel, liquibaseModel, new Slf4jLog(log),
                new File(rdbms2LiquibaseScriptResource.getSctiptRoot().getAbsolutePath(), "rdbms2liquibase/transformations"),
                "hsqldb" );

        return liquibaseModel;
    }
}
