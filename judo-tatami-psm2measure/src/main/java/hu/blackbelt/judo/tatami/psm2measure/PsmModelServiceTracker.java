package hu.blackbelt.judo.tatami.psm2measure;

import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
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
public class PsmModelServiceTracker extends AbstractModelTracker<PsmModel> {

    @Reference
    hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureSerivce psm2MeasureSerivce;

    Map<String, ServiceRegistration<MeasureModel>> registrations = new ConcurrentHashMap<>();
    Map<String, MeasureModel> models = new HashMap<>();


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
    public void install(PsmModel psmModel) {
        String key = psmModel.getName();
        MeasureModel measureModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + psmModel.getName());
            return;
        }

        try {
            measureModel = psm2MeasureSerivce
                    .install(psmModel, componentContext.getBundleContext());
            log.info("Registering model: " + measureModel);
            ServiceRegistration<MeasureModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(MeasureModel.class, measureModel, measureModel.toDictionary());
            models.put(key, measureModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register PSM Model: " + psmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(PsmModel psmModel) {
        String key = psmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModel.getName());
        } else {
            psm2MeasureSerivce.uninstall(psmModel);
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }

    @Override
    public Class<PsmModel> getModelClass() {
        return PsmModel.class;
    }


}
