package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.createLiquibaseResourceSet;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.createRdbmsResourceSet;

@Slf4j
public class Rdbms2LiquibaseTest {


    URIHandler uriHandler;
    Log slf4jlog;
    RdbmsModel rdbmsModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                srcDir().getAbsolutePath());

        // Default logger
        slf4jlog = new Slf4jLog(log);

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
        Resource  liquibaseResource = liquibaseResourceSet.createResource(liquibaseUri);

        LiquibaseModel liquibaseModel = LiquibaseModel.buildLiquibaseModel()
                .name(this.rdbmsModel.getName())
                .resource(liquibaseResource)
                .uri(liquibaseUri)
                .version(this.rdbmsModel.getVersion())
                .build();

        Rdbms2Liquibase.executeRdbms2LiquibaseTransformation(liquibaseResourceSet, rdbmsModel, liquibaseModel, new Slf4jLog(log),
                new File(srcDir().getAbsolutePath(), "epsilon/transformations"),
                "hsqldb");



        /*
        // Saving file
        String changeLogPath = new File(srcDir(), "/northwind-liquibase.changelog.xml").getAbsolutePath();
        XMLResource xmlResource = new XMLResourceImpl(URI.createFileURI(changeLogPath));
        xmlResource.getContents().addAll(EcoreUtil.copyAll(liquibaseResource.getContents()));
        for (EObject e : liquibaseResource.getContents()) {
            log.info(e.toString());
        }
        */

        final Map<Object, Object> saveOptions = new HashMap(); //liquibaseResource.getDefaultSaveOptions();
        saveOptions.put(XMLResource.OPTION_DECLARE_XML,Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF,XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        saveOptions.put(XMLResource.OPTION_SCHEMA_LOCATION,Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION,Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_SKIP_ESCAPE_URI,Boolean.FALSE);
        saveOptions.put(XMLResource.OPTION_ENCODING,"UTF-8");

        liquibaseResource.save(saveOptions);

        // Executing on HSQLDB

        /*
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
        Database liquibaseDb = new HsqlDatabase();
        liquibaseDb.setConnection(new HsqlConnection(connection));
        Liquibase liquibase = new Liquibase(new File(srcDir(), this.rdbmsModel.getName() + ".changelog.xml").getAbsolutePath(), new FileSystemResourceAccessor(), liquibaseDb);
        liquibase.update("");
        */
    }


    public File srcDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}