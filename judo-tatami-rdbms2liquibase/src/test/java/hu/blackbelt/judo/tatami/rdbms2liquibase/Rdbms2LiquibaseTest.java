package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.HsqlConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.DriverManager;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.createLiquibaseResourceSet;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.saveLiquibaseModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.createRdbmsResourceSet;
import static hu.blackbelt.judo.tatami.rdbms2liquibase.Rdbms2Liquibase.executeRdbms2LiquibaseTransformation;

@Slf4j
public class Rdbms2LiquibaseTest {


    URIHandler uriHandler;
    RdbmsModel rdbmsModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                targetDir().getAbsolutePath());

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet rdbmsesourceSet = createRdbmsResourceSet(uriHandler);
        rdbmsModel = RdbmsModelLoader.loadRdbmsModel(
                rdbmsesourceSet,
                URI.createURI("urn:northwind-rdbms.model"),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testRdbms2LiquibaseTransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet liquibaseResourceSet = createLiquibaseResourceSet(uriHandler);

        // Create wirtual URN
        URI liquibaseUri = URI.createURI("urn:" + this.rdbmsModel.getName() + ".changelog.xml");
        // Resource  liquibaseResource = liquibaseResourceSet.createResource(liquibaseUri);

        LiquibaseModel liquibaseModel = LiquibaseModel.buildLiquibaseModel()
                .name(this.rdbmsModel.getName())
                .resourceSet(liquibaseResourceSet)
                .uri(liquibaseUri)
                .version(this.rdbmsModel.getVersion())
                .build();

        executeRdbms2LiquibaseTransformation(liquibaseResourceSet, rdbmsModel, liquibaseModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations"),
                "hsqldb");

        saveLiquibaseModel(liquibaseModel);

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