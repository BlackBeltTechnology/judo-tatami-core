package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.util.Map;

import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.executeAsm2SDKGeneration;

@Component(immediate = true, service = Asm2SDKService.class)
@Slf4j
public class Asm2SDKService {

    Map<AsmModel, Bundle> asm2SdkBundles = Maps.newHashMap();

    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    public void install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {

            java.net.URI scriptUri =
                    this.bundleContext.getBundle()
                            .getEntry("/tatami/asm2sdk/templates/main.egl")
                            .toURI()
                            .resolve(".");

            asm2SdkBundles.put(asmModel,
                    bundleContext.installBundle(this.getClass().getName(),
                            executeAsm2SDKGeneration(asmModel, logger, scriptUri, new File(""))));
            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
        asm2SdkBundles.get(asmModel).start();
    }

    public void uninstall(AsmModel asmModel) throws BundleException {
        if (asm2SdkBundles.containsKey(asmModel)) {
            asm2SdkBundles.get(asmModel).uninstall();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
