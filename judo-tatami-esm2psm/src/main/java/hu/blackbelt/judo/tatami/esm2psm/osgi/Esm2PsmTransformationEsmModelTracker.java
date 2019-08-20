package hu.blackbelt.judo.tatami.esm2psm.osgi;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component(immediate = true)
@Slf4j
public class Esm2PsmTransformationEsmModelTracker extends AbstractModelTracker<EsmModel> {

    @Reference
    Esm2PsmTransformationService esm2PsmTransformationService;

    Map<String, ServiceRegistration<PsmModel>> registrations = new ConcurrentHashMap<>();
    Map<String, PsmModel> models = new HashMap<>();


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
        PsmModel psmModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + esmModel.getName());
            return;
        }

        try {
            psmModel = esm2PsmTransformationService.install(esmModel);
            log.info("Registering model: " + psmModel);
            ServiceRegistration<PsmModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(PsmModel.class, psmModel, psmModel.toDictionary());
            models.put(key, psmModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register PSM Model: " + esmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(EsmModel esmModel) {
        String key = esmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + esmModel.getName());
        } else {
            esm2PsmTransformationService.uninstall(esmModel);
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
