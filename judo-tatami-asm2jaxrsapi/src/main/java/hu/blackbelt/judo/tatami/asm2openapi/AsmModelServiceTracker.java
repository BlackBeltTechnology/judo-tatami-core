package hu.blackbelt.judo.tatami.asm2openapi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true)
@Slf4j
public class AsmModelServiceTracker extends AbstractModelInfoTracker<AsmModel> {

    @Reference
    Asm2JAXRSAPIService asm2JAXRSAPIService;


    @Activate
    protected void activate(ComponentContext contextPar) {
        componentContext = contextPar;
        openTracker(contextPar.getBundleContext());
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
        // registrations.forEach((k, v) -> { v.unregister(); });
    }

    private ComponentContext componentContext;

    @Override
    public void install(AsmModel asmModel) {
        /*
        String key = asmModel.getName();
        OpenAPIModel openAPIModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + asmModel.getName());
            return;
        }

        try {
            openAPIModel = asm2JAXRSAPIService
                    .install(asmModel, componentContext.getBundleContext());
            log.info("Registering model: " + openAPIModel);
            ServiceRegistration<OpenAPIModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(OpenAPIModel.class, openAPIModel, openAPIModel.toDictionary());
            models.put(key, openAPIModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register OpenAPI Model: " + asmModel.getName(), e);
        }

         */
    }

    @Override
    public void uninstall(AsmModel asmModel) {
        /*
        String key = asmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + asmModel.getName());
        } else {
            asm2JAXRSAPIService.uninstall(asmModel);
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }

         */
    }

    @Override
    public Class<AsmModel> getModelClass() {
        return AsmModel.class;
    }


}
