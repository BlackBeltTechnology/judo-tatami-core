package hu.blackbelt.judo.tatami.asm2keycloak.osgi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel;
import hu.blackbelt.judo.tatami.asm2keycloak.Asm2KeycloakTransformationTrace;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.tatami.asm2keycloak.Asm2Keycloak.executeAsm2KeycloakTransformation;

@Component(immediate = true, service = Asm2KeycloakTransformationSerivce.class)
@Slf4j
public class Asm2KeycloakTransformationSerivce {

    Map<AsmModel, ServiceRegistration<TransformationTrace>> asm2keycloakTransformationTraceRegistration = Maps.newHashMap();
    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    public KeycloakModel install(AsmModel asmModel) throws Exception {

        KeycloakModel keycloakModel = KeycloakModel.buildKeycloakModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("keycloak:" + asmModel.getName() + ".model"))
                .checksum(asmModel.getChecksum())
                .tags(asmModel.getTags())
                .build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    bundleContext.getBundle()
                            .getEntry("/tatami/asm2keycloak/transformations/asmToKeycloak.etl")
                            .toURI()
                            .resolve(".");

            Asm2KeycloakTransformationTrace asm2KeycloakTransformationTrace = executeAsm2KeycloakTransformation(
                    asmModel,
                    keycloakModel,
                    logger,
                    scriptUri);

            asm2keycloakTransformationTraceRegistration.put(asmModel,
                    bundleContext.registerService(TransformationTrace.class, asm2KeycloakTransformationTrace, new Hashtable<>()));

            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
        return keycloakModel;
    }

    public void uninstall(AsmModel asmModel) {
        if (asm2keycloakTransformationTraceRegistration.containsKey(asmModel)) {
            asm2keycloakTransformationTraceRegistration.get(asmModel).unregister();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
