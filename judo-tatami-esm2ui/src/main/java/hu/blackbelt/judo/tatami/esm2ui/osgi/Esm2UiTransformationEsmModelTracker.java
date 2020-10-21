package hu.blackbelt.judo.tatami.esm2ui.osgi;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
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
public class Esm2UiTransformationEsmModelTracker extends AbstractModelTracker<EsmModel> {

    @Reference
    Esm2UiTransformationService esm2UiTransformationService;

    Map<String, ServiceRegistration<UiModel>> registrations = new ConcurrentHashMap<>();
    Map<String, UiModel> models = new HashMap<>();


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
    public void install(EsmModel esmModel) {
        String key = esmModel.getName();
        UiModel uiModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + esmModel.getName());
            return;
        }

        try {
            // TODO: Handling application type and columns
        	uiModel = esm2UiTransformationService.install(esmModel, "default", 12);
            log.info("Registering model: " + uiModel);
            ServiceRegistration<UiModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(UiModel.class, uiModel, uiModel.toDictionary());
            models.put(key, uiModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register UI Model: " + esmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(EsmModel esmModel) {
        String key = esmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + esmModel.getName());
        } else {
            esm2UiTransformationService.uninstall(esmModel);
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }

    @Override
    public Class<EsmModel> getModelClass() {
        return EsmModel.class;
    }


}
