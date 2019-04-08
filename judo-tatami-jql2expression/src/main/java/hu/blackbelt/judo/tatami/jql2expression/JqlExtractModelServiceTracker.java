package hu.blackbelt.judo.tatami.jql2expression;

import com.google.common.collect.Maps;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component(immediate = true)
@Slf4j
public class JqlExtractModelServiceTracker {

    private final Map<ServiceReference, AsmModel> asmInstanceCache = Maps.newConcurrentMap();
    private final Map<ServiceReference, PsmJqlExtractModel> psmJqlExtractInstanceCache = Maps.newConcurrentMap();

    private final Map<String, AsmModel> registeredAsmModels = Maps.newConcurrentMap();
    private final Map<String, PsmJqlExtractModel> registeredPsmJqlExtractModels = Maps.newConcurrentMap();


    public void bindAsmService(ServiceReference serviceReference, AsmModel instance) {
        asmInstanceCache.put(serviceReference, instance);
        log.info("Preparing model for transformation: " + instance);
        final String key = instance.getName();

        registeredAsmModels.put(key, instance);
        final PsmJqlExtractModel psmJqlExtractModel = registeredPsmJqlExtractModels.get(key);

        if (psmJqlExtractModel != null) {
            install(key, instance, psmJqlExtractModel);
        }
    }

    public void unbindAsmService(ServiceReference serviceReference) {
        final AsmModel instance = asmInstanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        final String key = instance.getName();
        asmInstanceCache.remove(serviceReference);
        registeredAsmModels.remove(key, instance);
        log.info("Unbind transformation: " + instance);
        uninstall(instance);
    }

    public void bindJqlExtractService(ServiceReference serviceReference, PsmJqlExtractModel instance) {
        psmJqlExtractInstanceCache.put(serviceReference, instance);
        log.info("Preparing model for transformation: " + instance);
        final String key = instance.getName();

        registeredPsmJqlExtractModels.put(key, instance);
        final AsmModel asmModel = registeredAsmModels.get(key);

        if (asmModel != null) {
            install(key, asmModel, instance);
        }
    }

    public void unbindJqlExtractService(ServiceReference serviceReference) {
        final PsmJqlExtractModel instance = psmJqlExtractInstanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        final String key = instance.getName();
        psmJqlExtractInstanceCache.remove(serviceReference);
        registeredPsmJqlExtractModels.remove(key);
        log.info("Unbind transformation: " + instance);
        uninstall(instance);
    }

    private AsmInfoServiceTracker asmModelInfoServiceTracker;
    private PsmJqlExtractInfoServiceTracker psmJqlExtractModelInfoServiceTracker;

    @SneakyThrows(InvalidSyntaxException.class)
    public void openTracker(BundleContext bundleContext) {
        psmJqlExtractModelInfoServiceTracker = new PsmJqlExtractInfoServiceTracker(this, bundleContext, PsmJqlExtractModel.class);
        psmJqlExtractModelInfoServiceTracker.open(true);
        asmModelInfoServiceTracker = new AsmInfoServiceTracker(this, bundleContext, AsmModel.class);
        asmModelInfoServiceTracker.open(true);
    }

    public void closeTracker() {
        psmJqlExtractModelInfoServiceTracker.close();
        asmModelInfoServiceTracker.close();
    }

    public class PsmJqlExtractInfoServiceTracker extends ServiceTracker<PsmJqlExtractModel, PsmJqlExtractModel> {

        private JqlExtractModelServiceTracker abstractModelInfoTracker;

        PsmJqlExtractInfoServiceTracker(JqlExtractModelServiceTracker abstractModelInfoTracker, BundleContext bundleContext, Class<PsmJqlExtractModel> clazz) throws InvalidSyntaxException {
            super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
            this.abstractModelInfoTracker = abstractModelInfoTracker;
        }

        @Override
        public PsmJqlExtractModel addingService(ServiceReference<PsmJqlExtractModel> serviceReference) {
            if (serviceReference.isAssignableTo(super.context.getBundle(), PsmJqlExtractModel.class.getName())) {
                PsmJqlExtractModel instance = super.addingService(serviceReference);
                abstractModelInfoTracker.bindJqlExtractService(serviceReference, instance);
                return instance;
            }
            return null;
        }

        @Override
        public void removedService(ServiceReference<PsmJqlExtractModel> serviceReference, PsmJqlExtractModel service) {
            abstractModelInfoTracker.unbindJqlExtractService(serviceReference);
            super.removedService(serviceReference, service);
        }

        @Override
        public void modifiedService(ServiceReference<PsmJqlExtractModel> serviceReference,
                                    PsmJqlExtractModel service) {
            super.modifiedService(serviceReference, service);
        }

    }

    public class AsmInfoServiceTracker extends ServiceTracker<AsmModel, AsmModel> {

        private JqlExtractModelServiceTracker abstractModelInfoTracker;

        AsmInfoServiceTracker(JqlExtractModelServiceTracker abstractModelInfoTracker, BundleContext bundleContext, Class<AsmModel> clazz) throws InvalidSyntaxException {
            super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
            this.abstractModelInfoTracker = abstractModelInfoTracker;
        }

        @Override
        public AsmModel addingService(ServiceReference<AsmModel> serviceReference) {
            if (serviceReference.isAssignableTo(super.context.getBundle(), AsmModel.class.getName())) {
                AsmModel instance = super.addingService(serviceReference);
                abstractModelInfoTracker.bindAsmService(serviceReference, instance);
                return instance;
            }
            return null;
        }

        @Override
        public void removedService(ServiceReference<AsmModel> serviceReference, AsmModel service) {
            abstractModelInfoTracker.unbindAsmService(serviceReference);
            super.removedService(serviceReference, service);
        }

        @Override
        public void modifiedService(ServiceReference<AsmModel> serviceReference,
                                    AsmModel service) {
            super.modifiedService(serviceReference, service);
        }

    }



    @Reference
    JqlExtract2ExpressionService jqlExtract2ExpressionSerivce;

    Map<String, ServiceRegistration<ExpressionModel>> registrations = new ConcurrentHashMap<>();
    Map<String, ExpressionModel> models = new HashMap<>();


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

    public void install(String key, AsmModel asmModel, PsmJqlExtractModel jqlExtractModel) {
        ExpressionModel expressionModel;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + key);
            return;
        }

        try {
            expressionModel = jqlExtract2ExpressionSerivce
                    .install(asmModel, jqlExtractModel, componentContext.getBundleContext());
            log.info("Registering model: " + expressionModel);
            ServiceRegistration<ExpressionModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(ExpressionModel.class, expressionModel, expressionModel.toDictionary());
            models.put(key, expressionModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register Model: " + key, e);
        }
    }

    public void uninstall(AsmModel asmModel) {
        final String key = asmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + key);
        } else {
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }

    public void uninstall(PsmJqlExtractModel jqlExtractModel) {
        final String key = jqlExtractModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + key);
        } else {
            jqlExtract2ExpressionSerivce.uninstall(jqlExtractModel);
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }
}
