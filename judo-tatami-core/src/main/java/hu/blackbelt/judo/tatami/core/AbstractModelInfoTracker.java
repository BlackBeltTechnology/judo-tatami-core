package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Map;


@Slf4j
public abstract class AbstractModelInfoTracker<T> {

    private final Map<ServiceReference, T> instanceCache = Maps.newConcurrentMap();


    public void bindService(ServiceReference serviceReference, T instance) {
        instanceCache.put(serviceReference, instance);
        log.info("Preparing model for transformation: " + instance);
        install(instance);
    }

    public void unbindService(ServiceReference serviceReference) {
        T instance = instanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        instanceCache.remove(serviceReference);
        log.info("Unbind transformation: " + instance);
        uninstall(instance);
    }

    private InfoServiceTracker psmModelInfoServiceTracker;

    @SneakyThrows(InvalidSyntaxException.class)
    public void openTracker(BundleContext bundleContext) {
        psmModelInfoServiceTracker = new InfoServiceTracker(this, bundleContext, getModelInfoClass());
        psmModelInfoServiceTracker.open(true);
    }

    public void closeTracker() {
        psmModelInfoServiceTracker.close();
    }

    public abstract void install(T instance);

    public abstract void uninstall(T instance);

    public abstract Class<T> getModelInfoClass();

    public class InfoServiceTracker extends ServiceTracker<T, T> {

        private AbstractModelInfoTracker abstractModelInfoTracker;

        InfoServiceTracker(AbstractModelInfoTracker abstractModelInfoTracker, BundleContext bundleContext, Class<T> clazz) throws InvalidSyntaxException {
            super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
            this.abstractModelInfoTracker = abstractModelInfoTracker;
        }

        @Override
        public T addingService(ServiceReference<T> serviceReference) {
            if (serviceReference.isAssignableTo(super.context.getBundle(), getModelInfoClass().getName())) {
                T instance = super.addingService(serviceReference);
                abstractModelInfoTracker.bindService(serviceReference, instance);
                return instance;
            }
            return null;
        }

        @Override
        public void removedService(ServiceReference<T> serviceReference, T service) {
            abstractModelInfoTracker.unbindService(serviceReference);
            super.removedService(serviceReference, service);
        }

        @Override
        public void modifiedService(ServiceReference<T> serviceReference,
                                    T service) {
            super.modifiedService(serviceReference, service);
        }

    }
}
