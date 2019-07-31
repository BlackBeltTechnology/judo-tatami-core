package hu.blackbelt.judo.tatami.psm2measure;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static hu.blackbelt.judo.meta.measure.support.MeasureModelResourceSupport.measureModelResourceSupportBuilder;
import static hu.blackbelt.judo.tatami.psm2measure.Psm2Measure.executePsm2MeasureTransformation;


@Component(immediate = true, service = Psm2MeasureSerivce.class)
@Slf4j
public class Psm2MeasureSerivce {

    public static final String MEASURE_META_VERSION_RANGE = "Measure-Meta-Version-Range";

    Map<PsmModel, ServiceRegistration<TransformationTrace>> psm2MeasureTransformationTraceRegistration = Maps.newHashMap();

    BundleContext scriptBundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        scriptBundleContext = bundleContext;
    }

    public MeasureModel install(PsmModel psmModel, BundleContext bundleContext) throws Exception {

        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "", bundleContext.getBundle());

        MeasureModel measureModel = MeasureModel.buildMeasureModel()
                .name(psmModel.getName())
                .version(psmModel.getVersion())
                .uri(URI.createURI("urn:" + psmModel.getName() + ".measure"))
                .checksum(psmModel.getChecksum())
                .measureModelResourceSupport(
                        measureModelResourceSupportBuilder()
                                .uriHandler(Optional.of(bundleURIHandler))
                                .build())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(MEASURE_META_VERSION_RANGE)).build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/psm2measure/transformations/measure/psmToMeasure.etl")
                            .toURI()
                            .resolve(".");

            Psm2MeasureTransformationTrace psm2MeasureTransformationTrace = executePsm2MeasureTransformation(measureModel.getResourceSet(), psmModel, measureModel, logger,
                    scriptUri);

            psm2MeasureTransformationTraceRegistration.put(psmModel, bundleContext.registerService(TransformationTrace.class, psm2MeasureTransformationTrace, new Hashtable<>()));
            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }

        return measureModel;
    }

    public void uninstall(PsmModel psmModel) {
        if (psm2MeasureTransformationTraceRegistration.containsKey(psmModel)) {
            psm2MeasureTransformationTraceRegistration.get(psmModel).unregister();
        } else {
            log.error("PSM model is not installed: " + psmModel.toString());
        }
    }
}
