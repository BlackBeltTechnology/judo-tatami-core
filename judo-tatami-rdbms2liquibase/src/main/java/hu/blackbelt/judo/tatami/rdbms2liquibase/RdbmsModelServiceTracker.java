package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
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
public class RdbmsModelServiceTracker extends AbstractModelInfoTracker<RdbmsModel> {

    @Reference
    Rdbms2LiquibaseSerivce psm2LiquibaseSerivce;

    Map<String, ServiceRegistration<hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel>> registrations = new ConcurrentHashMap<>();
    Map<String, hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel> models = new HashMap<>();


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
    public void install(RdbmsModel psmModel) {
        String key = psmModel.getName();
        hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel liquibaseModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + psmModel.getName());
            return;
        }

        try {
            liquibaseModel = psm2LiquibaseSerivce
                    .install(psmModel, componentContext.getBundleContext());
            log.info("Registering model: " + liquibaseModel);
            ServiceRegistration<LiquibaseModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(LiquibaseModel.class, liquibaseModel, liquibaseModel.toDictionary());
            models.put(key, liquibaseModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register PSM Model: " + psmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(RdbmsModel psmModel) {
        String key = psmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModel.getName());
        } else {
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }

    @Override
    public Class<RdbmsModel> getModelClass() {
        return RdbmsModel.class;
    }


}
