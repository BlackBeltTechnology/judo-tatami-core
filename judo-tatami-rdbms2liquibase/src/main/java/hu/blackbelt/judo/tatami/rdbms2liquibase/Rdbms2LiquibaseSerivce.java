package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseNamespaceFixUriHandler;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Optional;

import static hu.blackbelt.judo.meta.liquibase.support.LiquibaseModelResourceSupport.liquibaseModelResourceSupportBuilder;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;

@Component(immediate = true, service = Rdbms2LiquibaseSerivce.class)
@Slf4j
public class Rdbms2LiquibaseSerivce {

    public static final String LIQUIBASE_META_VERSION_RANGE = "Liquibase-Meta-Version-Range";

    BundleContext scriptBundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        scriptBundleContext = bundleContext;
    }

    public LiquibaseModel install(RdbmsModel rdbmsModel, BundleContext bundleContext) throws Exception {
        URIHandler liquibaseNamespaceFixUriHandlerFromBundle =
                new LiquibaseNamespaceFixUriHandler(
                        new BundleURIHandler("urn", "", bundleContext.getBundle())
                );

        URI liquibasUri = URI.createURI("urn:" + rdbmsModel.getName() + ".changlelog.xml");

        //new LiquibaseNamespaceFixUriHandler(
        LiquibaseModel liquibaseModel = LiquibaseModel.buildLiquibaseModel()
                .name(rdbmsModel.getName())
                .version(rdbmsModel.getVersion())
                .uri(liquibasUri)
                .checksum(rdbmsModel.getChecksum())
                .liquibaseModelResourceSupport(
                        liquibaseModelResourceSupportBuilder()
                                .uriHandler(Optional.of(liquibaseNamespaceFixUriHandlerFromBundle))
                                .build())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(LIQUIBASE_META_VERSION_RANGE)).build();


        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/rdbms2liquibase/transformations/rdbmsToLiquibase.etl")
                            .toURI()
                            .resolve(".");

            executeRdbms2LiquibaseTransformation(liquibaseModel.getResourceSet(), rdbmsModel, liquibaseModel, logger,
                    scriptUri,
                    "hsqldb" );

            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }

        return liquibaseModel;
    }
}
