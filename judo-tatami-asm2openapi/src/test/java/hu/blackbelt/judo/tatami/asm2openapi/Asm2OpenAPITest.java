package hu.blackbelt.judo.tatami.asm2openapi;

import edu.uoc.som.openapi.Root;
import edu.uoc.som.openapi.io.OpenAPIExporter;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.LocalAsmPackageRegistration;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.util.Map;

import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.*;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;

@Slf4j
public class Asm2OpenAPITest {


    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                srcDir().getAbsolutePath());

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler, new LocalAsmPackageRegistration());
        asmModel = AsmModelLoader.loadAsmModel(
                asmResourceSet,
                URI.createURI("urn:northwind-asm.model"),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testAsm2OpenAPITransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet openAPIResourceSet = createOpenAPIResourceSet(uriHandler);

        // Create wirtual URN
        URI openAPIUri = URI.createURI("urn:" + asmModel.getName() + ".openapi");
        Resource openAPIResource = openAPIResourceSet.createResource(openAPIUri);

        OpenAPIModel openAPIModel = OpenAPIModel.buildOpenAPIModel()
                .name(asmModel.getName())
                .resource(openAPIResource)
                .uri(openAPIUri)
                .version(asmModel.getVersion())
                .build();

        executeAsm2OpenAPITransformation(openAPIResourceSet, asmModel, openAPIModel, new Slf4jLog(log),
                new File(srcDir().getAbsolutePath(), "epsilon/transformations/openapi"));

        openAPIModel.getResource().getContents().forEach(m -> {
            final String title = ((Root)m).getApi().getInfo().getTitle();
            final File targetFile = new File(srcDir().getAbsolutePath()+"/northwind-openapi-" + title + ".json");
            try (final Writer targetFileWriter = new FileWriter(targetFile)) {
                final String json = OpenAPIExporter.getJsonFromSwaggerModel((Root) m).toString();
                targetFileWriter.append(json);
            } catch (IOException ex) {
                log.error("Unable to create JSON output", ex);
            }
        });

        XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI(srcDir().getAbsolutePath()+"/northwind-openapi.model"));
        xmiResource.getContents().addAll(EcoreUtil.copyAll(openAPIResource.getContents()));
        for (EObject e : openAPIResource.getContents()) {
            log.debug(e.toString());
        }

        final Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
        saveOptions.put(XMIResource.OPTION_DECLARE_XML,Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION,Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION,Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI,Boolean.FALSE);
        saveOptions.put(XMIResource.OPTION_ENCODING,"UTF-8");

        xmiResource.save(saveOptions);
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
