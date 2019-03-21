package hu.blackbelt.judo.tatami.psm2measure;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader;
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
import java.nio.file.FileSystems;
import java.util.Map;

import static hu.blackbelt.judo.meta.measure.runtime.MeasureModelLoader.*;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.createPsmResourceSet;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;

@Slf4j
public class Psm2MeasureTest {


    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                srcDir().getAbsolutePath());

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet psmResourceSet = createPsmResourceSet(uriHandler);
        psmModel = PsmModelLoader.loadPsmModel(
                psmResourceSet,
                URI.createURI("urn:northwind-judopsm.model"),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testPsm2MeasureTransformation() throws Exception {

        // Creating ASM resource set.
        ResourceSet measureResourceSet = createMeasureResourceSet(uriHandler);

        // Create wirtual URN
        URI measureUri = URI.createURI("urn:" + psmModel.getName() + ".measure");
        Resource  measureResource = measureResourceSet.createResource(measureUri);

        MeasureModel measureModel = MeasureModel.buildMeasureModel()
                .name(psmModel.getName())
                .resource(measureResource)
                .uri(measureUri)
                .version(psmModel.getVersion())
                .build();

        executePsm2MeasureTransformation(measureResourceSet, psmModel, measureModel, new Slf4jLog(log),
                new File(srcDir().getAbsolutePath(), "epsilon/transformations/measure"));


    /*
        TreeIterator<Notifier> iter = measureResourceSet.getAllContents();
        while (iter.hasNext()) {
            final Notifier obj = iter.next();
            log.debug(obj.toString());
        } */


        XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI(srcDir().getAbsolutePath()+"/northwind-measure.model"));
        xmiResource.getContents().addAll(EcoreUtil.copyAll(measureResource.getContents()));
        for (EObject e : measureResource.getContents()) {
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