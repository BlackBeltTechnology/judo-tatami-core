package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.Maps;
import hu.blackbelt.judo.meta.psm.PsmModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Map;


@Component(immediate = true)
@Slf4j
public class PsmModelTracker {

    private final Map<ServiceReference, PsmModel> instanceCache = Maps.newConcurrentMap();


    @Reference
    Psm2AsmTransformation psm2AsmTransformation;

    @Activate
    protected void activate(BundleContext contextPar) {
        openTracker(contextPar);
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
    }

    public void bindService(ServiceReference serviceReference, PsmModel instance) {
        instanceCache.put(serviceReference, instance);
        log.info("Preparing model for ASM transformation: " + instance);
    }

    public void unbindService(ServiceReference serviceReference) {
        PsmModel instance = instanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        instanceCache.remove(serviceReference);
        log.info("Unbind ASM transformation: " + instance);
    }

    private  PsmModelsServiceTracker psmModelsServiceTracker;

    @SneakyThrows(InvalidSyntaxException.class)
    public void openTracker(BundleContext bundleContext) {
        psmModelsServiceTracker = new PsmModelsServiceTracker(bundleContext, PsmModel.class);
        psmModelsServiceTracker.open(true);
    }

    public void closeTracker() {
        psmModelsServiceTracker.close();
    }


    private final class PsmModelsServiceTracker extends ServiceTracker<PsmModel, PsmModel> {

        private PsmModelsServiceTracker(BundleContext bundleContext, Class<PsmModel> clazz) throws InvalidSyntaxException {
            super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
        }

        @Override
        public PsmModel addingService(ServiceReference<PsmModel> serviceReference) {
            if (serviceReference.isAssignableTo(super.context.getBundle(), PsmModel.class.getName())) {
                PsmModel instance = super.addingService(serviceReference);
                bindService(serviceReference, instance);
                return instance;
            }
            return null;
        }

        @Override
        public void removedService(ServiceReference<PsmModel> serviceReference, PsmModel service) {
            unbindService(serviceReference);
            super.removedService(serviceReference, service);
        }

        @Override
        public void modifiedService(ServiceReference<PsmModel> serviceReference,
                                    PsmModel service) {
            super.modifiedService(serviceReference, service);
        }

    }

}
