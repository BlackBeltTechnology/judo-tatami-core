package hu.blackbelt.judo.tatami.jql2expression;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModelLoader;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
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
import java.nio.file.FileSystems;
import java.util.Map;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.createPsmJqlExtractResourceSet;
import static hu.blackbelt.judo.tatami.jql2expression.JqlExtract2Expression.*;

@Slf4j
public class JqlExtract2ExpressionTest {

    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;
    PsmJqlExtractModel jqlExtractModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                srcDir().getAbsolutePath());

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet jqlExtractResourceSet = createPsmJqlExtractResourceSet(uriHandler);
        jqlExtractModel = PsmJqlExtractModelLoader.loadPsmJqlExtractModel(
                jqlExtractResourceSet,
                URI.createURI("urn:northwind-jqlextract.model"),
                "northwind",
                "1.0.0");

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler);
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
    public void testJqlExtract2ExpressionTransformation() throws Exception {

        // Creating expression resource set.
        ResourceSet expressionResourceSet = ExpressionModelLoader.createExpressionResourceSet(uriHandler);

        // Create virtual URN
        URI expressionUri = URI.createURI("urn:" + jqlExtractModel.getName() + ".expr");
        Resource expressionResource = expressionResourceSet.createResource(expressionUri);

        ExpressionModel expressionModel = ExpressionModel.buildExpressionModel()
                .name(jqlExtractModel.getName())
                .resource(expressionResource)
                .uri(expressionUri)
                .version(jqlExtractModel.getVersion())
                .build();

        executeJqlExtract2ExpressionTransformation(expressionResourceSet, asmModel, jqlExtractModel, expressionModel,
                new Slf4jLog(log), new File(srcDir().getAbsolutePath(), "epsilon/transformations/expression"));


        TreeIterator<Notifier> iter = expressionResourceSet.getAllContents();
        while (iter.hasNext()) {
            final Notifier obj = iter.next();
            log.debug(obj.toString());
        }

        XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI(srcDir().getAbsolutePath()+"/northwind-expression.model"));
        xmiResource.getContents().addAll(EcoreUtil.copyAll(expressionResource.getContents()));
        for (EObject e : expressionResource.getContents()) {
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
