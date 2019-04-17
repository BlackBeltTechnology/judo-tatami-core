package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
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
public class AsmModelServiceTracker extends AbstractModelTracker<AsmModel> {

    @Reference
    Asm2RdbmsSerivce asm2RdbmsSerivce;

    Map<String, ServiceRegistration<RdbmsModel>> registrations = new ConcurrentHashMap<>();
    Map<String, RdbmsModel> models = new HashMap<>();


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
        RdbmsModel rdbmsModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + asmModel.getName());
            return;
        }

        try {
            // TODO: Handling dialect
            rdbmsModel = asm2RdbmsSerivce
                    .install(asmModel, componentContext.getBundleContext(), "hsqldb");
            log.info("Registering model: " + rdbmsModel);
            ServiceRegistration<RdbmsModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(RdbmsModel.class, rdbmsModel, rdbmsModel.toDictionary());
            models.put(key, rdbmsModel);
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
            asm2RdbmsSerivce.uninstall(asmModel);
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
