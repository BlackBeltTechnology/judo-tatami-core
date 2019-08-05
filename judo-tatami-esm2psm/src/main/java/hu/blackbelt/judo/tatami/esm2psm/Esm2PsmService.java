package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
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

import static hu.blackbelt.judo.meta.psm.support.PsmModelResourceSupport.psmModelResourceSupportBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;

@Component(immediate = true, service = Esm2PsmService.class)
@Slf4j
public class Esm2PsmService {

    Map<EsmModel, ServiceRegistration<TransformationTrace>> esm2PsmTransformationTraceRegistration = Maps.newHashMap();

    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public PsmModel install(EsmModel esmModel) throws Exception {
        PsmModel psmModel = PsmModel.buildPsmModel()
                .name(esmModel.getName())
                .version(esmModel.getVersion())
                .uri(URI.createURI("psm:" + esmModel.getName() + ".model"))
                .checksum(esmModel.getChecksum())
                .build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));

        java.net.URI scriptUri =
                bundleContext.getBundle()
                        .getEntry("/tatami/esm2psm/transformations/psm/esmToPsm.etl")
                        .toURI()
                        .resolve(".");
        try {
            Esm2PsmTransformationTrace transformationTrace = executeEsm2PsmTransformation(
                    esmModel,
                    psmModel,
                    logger,
                    scriptUri);

            esm2PsmTransformationTraceRegistration.put(esmModel,
                    bundleContext.registerService(TransformationTrace.class, transformationTrace, new Hashtable<>()));
            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
        return psmModel;
    }


    public void uninstall(EsmModel esmModel) {
        if (esm2PsmTransformationTraceRegistration.containsKey(esmModel)) {
            esm2PsmTransformationTraceRegistration.get(esmModel).unregister();
        } else {
            log.error("PSM model is not installed: " + esmModel.toString());
        }
    }

}
