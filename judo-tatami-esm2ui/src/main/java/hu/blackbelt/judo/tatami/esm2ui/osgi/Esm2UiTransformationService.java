package hu.blackbelt.judo.tatami.esm2ui.osgi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import hu.blackbelt.judo.tatami.esm2ui.Esm2UiTransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;

@Component(immediate = true, service = Esm2UiTransformationService.class)
@Slf4j
public class Esm2UiTransformationService {

    Map<EsmModel, ServiceRegistration<TransformationTrace>> esm2UiTransformationTraceRegistration = Maps.newHashMap();

    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public UiModel install(EsmModel esmModel, String applicationType, Integer applicationColumns) throws Exception {
        UiModel uiModel = UiModel.buildUiModel()
                .name(esmModel.getName())
                .version(esmModel.getVersion())
                .uri(URI.createURI("ui:" + esmModel.getName() + ".model"))
                .checksum(esmModel.getChecksum())
                .tags(esmModel.getTags())
                .build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));

        java.net.URI scriptUri =
                bundleContext.getBundle()
                        .getEntry("/tatami/esm2ui/transformations/ui/esmToUi.etl")
                        .toURI()
                        .resolve(".");
        try {
            Esm2UiTransformationTrace transformationTrace = executeEsm2UiTransformation(
                    esmModel,
                    applicationType,
                    applicationColumns,
                    uiModel,
                    logger,
                    scriptUri);

            esm2UiTransformationTraceRegistration.put(esmModel,
                    bundleContext.registerService(TransformationTrace.class, transformationTrace, new Hashtable<>()));
            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
        return uiModel;
    }


    public void uninstall(EsmModel esmModel) {
        if (esm2UiTransformationTraceRegistration.containsKey(esmModel)) {
            esm2UiTransformationTraceRegistration.get(esmModel).unregister();
        } else {
            log.error("UI model is not installed: " + esmModel.toString());
        }
    }

}
