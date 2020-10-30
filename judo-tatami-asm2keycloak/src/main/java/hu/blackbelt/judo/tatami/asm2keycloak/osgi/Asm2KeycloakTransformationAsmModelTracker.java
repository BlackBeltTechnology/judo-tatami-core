package hu.blackbelt.judo.tatami.asm2keycloak.osgi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Asm2KeycloakTransformationAsmModelTracker extends AbstractModelTracker<AsmModel> {

    @Reference
    Asm2KeycloakTransformationSerivce asm2KeycloakTransformationSerivce;

    Map<String, ServiceRegistration<KeycloakModel>> registrations = new ConcurrentHashMap<>();
    Map<String, KeycloakModel> models = new HashMap<>();


    @Activate
    protected void activate(ComponentContext contextPar) {
        componentContext = contextPar;
        openTracker(contextPar.getBundleContext());
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
        registrations.forEach((k, v) -> { v.unregister(); });
    }

    private ComponentContext componentContext;

    @Override
    public void install(AsmModel asmModel) {
        String key = asmModel.getName();
        KeycloakModel keycloakModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + asmModel.getName());
            return;
        }

        try {
            // TODO: Handling dialect
            keycloakModel = asm2KeycloakTransformationSerivce.install(asmModel);
            log.info("Registering model: " + keycloakModel);
            ServiceRegistration<KeycloakModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(KeycloakModel.class, keycloakModel, keycloakModel.toDictionary());
            models.put(key, keycloakModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register PSM Model: " + asmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(AsmModel asmModel) {
        String key = asmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + asmModel.getName());
        } else {
            asm2KeycloakTransformationSerivce.uninstall(asmModel);
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }

    @Override
    public Class<AsmModel> getModelClass() {
        return AsmModel.class;
    }


}
