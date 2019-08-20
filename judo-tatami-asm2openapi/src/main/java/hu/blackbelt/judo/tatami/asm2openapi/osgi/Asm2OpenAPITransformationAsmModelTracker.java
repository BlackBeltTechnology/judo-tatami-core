package hu.blackbelt.judo.tatami.asm2openapi.osgi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
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
public class Asm2OpenAPITransformationAsmModelTracker extends AbstractModelTracker<AsmModel> {

    @Reference
    Asm2OpenAPITransformationService asm2OpenAPITransformationService;

    Map<String, ServiceRegistration<OpenapiModel>> registrations = new ConcurrentHashMap<>();
    Map<String, OpenapiModel> models = new HashMap<>();


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
        OpenapiModel openAPIModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + asmModel.getName());
            return;
        }

        try {
            openAPIModel = asm2OpenAPITransformationService.install(asmModel);
            log.info("Registering model: " + openAPIModel);
            ServiceRegistration<OpenapiModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(OpenapiModel.class, openAPIModel, openAPIModel.toDictionary());
            models.put(key, openAPIModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register OpenAPI Model: " + asmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(AsmModel asmModel) {
        String key = asmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + asmModel.getName());
        } else {
            asm2OpenAPITransformationService.uninstall(asmModel);
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
