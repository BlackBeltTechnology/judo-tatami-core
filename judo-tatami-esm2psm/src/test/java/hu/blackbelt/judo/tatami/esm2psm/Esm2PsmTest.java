package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmModelLoader;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.createPsmResourceSet;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.savePsmModel;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModelLoader.createEsmResourceSet;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.*;

@Slf4j
public class Esm2PsmTest {

    public static final String ESM_2_PSM_MODEL = "esm2psm.model";
    public static final String TRACE_ESM_2_PSM = "trace:esm2esm";
    public static final String PSM_NORTHWIND = "psm:northwind";
    public static final String ESM_NORTHWIND = "esm:northwind";
    public static final String URN_NORTHWIND_ESM = "urn:northwind-esm.model";
    public static final String URN_NORTHWIND_PSM = "urn:northwind.psm";

    URIHandler uriHandler;
    Log slf4jlog;
    EsmModel esmModel;

    @Before
    public void setUp() throws Exception {

        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), targetDir().getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(ESM_NORTHWIND), URI.createURI(URN_NORTHWIND_ESM),
                        URI.createURI(PSM_NORTHWIND), URI.createURI(URN_NORTHWIND_PSM))
        );

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet esmResourceSet = createEsmResourceSet(uriHandler);
        esmModel = EsmModelLoader.loadEsmModel(
                esmResourceSet,
                URI.createURI(ESM_NORTHWIND),
                "northwind",
                "1.0.0");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testEsm2PsmTransformation() throws Exception {

        // Creating PSM resource set.
        ResourceSet psmResourceSet = createPsmResourceSet(uriHandler);

        // Creating PsmModel definition
        PsmModel psmModel = PsmModel.buildPsmModel()
                .name(esmModel.getName())
                .resourceSet(psmResourceSet)
                .uri(URI.createURI(PSM_NORTHWIND))
                .version(esmModel.getVersion())
                .build();


        // Make transformation which returns the tracr with the serialized URI's
        Esm2PsmTransformationTrace esm2PsmTransformationTrace = executeEsm2PsmTransformation(psmResourceSet, esmModel, psmModel, new Slf4jLog(log),
                new File(targetDir().getAbsolutePath(), "epsilon/transformations/psm"));

        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getEsm2PsmTrace(esm2PsmTransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(targetDir().getAbsolutePath(), ESM_2_PSM_MODEL)), ImmutableMap.of());

        // Loadeing trace map
        ResourceSet traceLoadedResourceSet = createEsm2PsmTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_ESM_2_PSM));
        traceResoureLoaded.load(new FileInputStream(new File(targetDir().getAbsolutePath(), ESM_2_PSM_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveEsm2PsmTrace(traceResoureLoaded, esmModel, psmModel);

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        savePsmModel(psmModel);
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
