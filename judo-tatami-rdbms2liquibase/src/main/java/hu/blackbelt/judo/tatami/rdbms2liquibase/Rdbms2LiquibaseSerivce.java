package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.createLiquibaseResourceSet;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.registerLiquibaseMetamodel;
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

        ResourceSet liquibaseResourceSet = createLiquibaseResourceSet(bundleURIHandler);
        registerLiquibaseMetamodel(liquibaseResourceSet);

        URI liquibasUri = URI.createURI("urn:" + rdbmsModel.getName() + ".changlelog.xml");
        Resource liquibaseResource = liquibaseResourceSet.createResource(liquibasUri);

        LiquibaseModel liquibaseModel = LiquibaseModel.buildLiquibaseModel()
                .name(rdbmsModel.getName())
                .resourceSet(liquibaseResourceSet)
                .uri(liquibasUri)
                .version(rdbmsModel.getVersion())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(LIQUIBASE_META_VERSION_RANGE))
                .build();


        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            executeRdbms2LiquibaseTransformation(liquibaseResourceSet, rdbmsModel, liquibaseModel, logger,
                    new File(rdbms2LiquibaseScriptResource.getSctiptRoot().getAbsolutePath(), "rdbms2liquibase/transformations"),
                    "hsqldb" );

            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }

        return liquibaseModel;
    }
}
