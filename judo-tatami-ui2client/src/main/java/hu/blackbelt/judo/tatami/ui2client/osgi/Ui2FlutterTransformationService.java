package hu.blackbelt.judo.tatami.ui2client.osgi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.ui2client.ClientTemplateProvider;
import hu.blackbelt.judo.tatami.ui2client.flutter.FlutterTemplateProvider;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static hu.blackbelt.judo.tatami.ui2client.Ui2Client.executeUi2ClientGenerationAsZip;

@Component(immediate = true, service = Ui2FlutterTransformationService.class)
@Slf4j
public class Ui2FlutterTransformationService {

    File tempDir;

    Map<UiModel, Map<Application, InputStream>> ui2FlutterZipStreams = Maps.newHashMap();

    BundleContext scriptBundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        tempDir = bundleContext.getBundle().getDataFile("generated");
        tempDir.mkdirs();
        scriptBundleContext = bundleContext;
    }

    @Deactivate
    public void deactivate(BundleContext bundleContext) throws IOException {
    }

    public void install(UiModel uiModel, BundleContext bundleContext) throws Exception {
        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {

            java.net.URI scriptUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/ui2flutter/templates/main.egl")
                            .toURI()
                            .resolve(".");

            ClientTemplateProvider clientTemplateProvider = new FlutterTemplateProvider();
            ui2FlutterZipStreams.put(uiModel, executeUi2ClientGenerationAsZip(uiModel, clientTemplateProvider.get(), logger,
                                    scriptUri));
            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
    }

    public void uninstall(UiModel uiModel) throws BundleException {
        if (ui2FlutterZipStreams.containsKey(uiModel)) {
            ui2FlutterZipStreams.remove(uiModel);
        } else {
            log.error("UI model is not installed: " + uiModel.toString());
        }
    }
}
