package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseNamespaceFixUriHandler;
import hu.blackbelt.judo.meta.liquibase.support.LiquibaseModelResourceSupport;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.support.RdbmsModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.HsqlConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;

@Slf4j
public class Rdbms2LiquibaseTest {

    public static final String LIQUIBASE_NORTHWIND = "liquibase:northwind";
    public static final String RDBMS_NORTHWIND = "rdbms:northwind";
    public static final String URN_NORTHWIND_RDBMS = "urn:northwind-rdbms.model";
    public static final String URN_NORTHWIND_LIQUIBASE = "urn:northwind.changelog.xml";
    public static final String NORTHWIND = "northwind";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    URIHandler uriHandler;
    RdbmsModel rdbmsModel;
    LiquibaseModel liquibaseModel;

    @Before
    public void setUp() throws Exception {

        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), new File(TARGET_TEST_CLASSES).getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(RDBMS_NORTHWIND), URI.createURI(URN_NORTHWIND_RDBMS),
                        URI.createURI(LIQUIBASE_NORTHWIND), URI.createURI(URN_NORTHWIND_LIQUIBASE))
        );


        // Loading RDBMS to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        RdbmsModelResourceSupport rdbmsModelResourceSupport = RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder()
                .uriHandler(Optional.of(uriHandler))
                .resourceSet(RdbmsModelResourceSupport.createRdbmsResourceSet())
                .build();

        // The RDBMS model resourceset have to know the mapping models
        RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel(rdbmsModelResourceSupport.getResourceSet());
        RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel(rdbmsModelResourceSupport.getResourceSet());
        RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel(rdbmsModelResourceSupport.getResourceSet());

        rdbmsModel = RdbmsModel.loadRdbmsModel(RdbmsModel.LoadArguments.loadArgumentsBuilder()
                .uri(URI.createURI(RDBMS_NORTHWIND))
                .rdbmsModelResourceSupport(Optional.of(rdbmsModelResourceSupport))
                .name(NORTHWIND)
                .build());

        // Create empty LIQUIBASE model
        LiquibaseModelResourceSupport liquibaseModelResourceSupport = LiquibaseModelResourceSupport.liquibaseModelResourceSupportBuilder()
                .uriHandler(Optional.of(new LiquibaseNamespaceFixUriHandler(uriHandler)))
//                .uriHandler(Optional.of(uriHandler))
                .build();

        liquibaseModel = LiquibaseModel.buildLiquibaseModel()
                .liquibaseModelResourceSupport(liquibaseModelResourceSupport)
                .name(NORTHWIND)
                .uri(URI.createURI(LIQUIBASE_NORTHWIND))
                .build();
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testRdbms2LiquibaseTransformation() throws Exception {

        executeRdbms2LiquibaseTransformation(liquibaseModel.getResourceSet(), rdbmsModel, liquibaseModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations").toURI(),
                "hsqldb");

        liquibaseModel.saveLiquibaseModel();

        // Executing on HSQLDB
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
        Database liquibaseDb = new HsqlDatabase();
        liquibaseDb.setConnection(new HsqlConnection(connection));
        Liquibase liquibase = new Liquibase(new File(targetDir(), this.rdbmsModel.getName() + ".changelog.xml").getAbsolutePath(), new FileSystemResourceAccessor(), liquibaseDb);
        liquibase.update("full,1.0.0");
    }


    public File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}