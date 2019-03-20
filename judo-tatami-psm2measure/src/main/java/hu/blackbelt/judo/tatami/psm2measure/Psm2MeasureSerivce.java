package hu.blackbelt.judo.tatami.psm2measure;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.measure.runtime.MeasureModelLoader.createMeasureResourceSet;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModelLoader.loadMeasureModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.registerPsmMetamodel;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;


@Component(immediate = true, service = Psm2MeasureSerivce.class)
@Slf4j
public class Psm2MeasureSerivce {

    public static final String MEASURE_META_VERSION_RANGE = "Measure-Meta-Version-Range";

    @Reference
    hu.blackbelt.judo.tatami.psm2asm.Psm2MeasureScriptResource psm2MeasureScriptResource;


    public MeasureModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createMeasureResourceSet(bundleURIHandler);

        MeasureModel asmModel = loadMeasureModel(resourceSet, URI.createURI("urn:" + psmModel.getName()),
                psmModel.getName(), psmModel.getVersion(), psmModel.getChecksum(),
                bundleContext.getBundle().getHeaders().get(MEASURE_META_VERSION_RANGE));

        registerPsmMetamodel(resourceSet);

        executePsm2MeasureTransformation(resourceSet, psmModel, asmModel, new Slf4jLog(log),
                new File(psm2MeasureScriptResource.getSctiptRoot().getAbsolutePath(), "psm2measure/transformations/measure") );

        return asmModel;
    }
}