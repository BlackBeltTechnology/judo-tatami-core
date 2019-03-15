package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.judo.meta.asm.AsmModel;
import hu.blackbelt.judo.meta.psm.PsmModel;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
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
public class PsmModelInfoServiceTracker extends AbstractModelInfoTracker<PsmModel> {

    @Reference
    Psm2AsmTransformationSerivce psm2AsmTransformationSerivce;

    Map<String, ServiceRegistration<AsmModel>> asmRegistrations = new ConcurrentHashMap<>();
    Map<String, AsmModel> asmModels = new HashMap<>();


    @Activate
    protected void activate(ComponentContext contextPar) {
        componentContext = contextPar;
        openTracker(contextPar.getBundleContext());
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
        asmRegistrations.forEach((k, v) -> { v.unregister(); });
    }

    private ComponentContext componentContext;

    @Override
    public void install(PsmModel psmModel) {
        String key = psmModel.getName();
        AsmModel asmModel = null;
        try {
            asmModel = psm2AsmTransformationSerivce
                    .install(psmModel, componentContext.getBundleContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Registering model: " + asmModel);
        ServiceRegistration<AsmModel> modelServiceRegistration =
                componentContext.getBundleContext()
                        .registerService(AsmModel.class, asmModel, asmModel.toDictionary());
        asmModels.put(key, asmModel);
    }

    @Override
    public void uninstall(PsmModel psmModel) {
        String key = psmModel.getName();
        if (!asmRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModel.getName());
        } else {
            asmRegistrations.get(key).unregister();
            asmRegistrations.remove(key);
            asmModels.remove(key);
        }
    }

    @Override
    public Class<PsmModel> getModelInfoClass() {
        return PsmModel.class;
    }


}
