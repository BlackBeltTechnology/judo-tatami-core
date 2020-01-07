package hu.blackbelt.judo.tatami.workflow.osgi;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Maps;

import hu.blackbelt.judo.tatami.core.TransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

/**
 * This class add abstract methods to handle the OSGi lifecycle of transformed models / traces / bundles
 */
@Slf4j
public abstract class AbstractTransformationContextRegistrationService implements TransformationContextRegistrationService {

    Map<Object, ServiceRegistration> serviceRegistrationMap = Maps.newConcurrentMap();
    Map<Object, Bundle> bundleRegistrationMap = Maps.newConcurrentMap();

    BundleContext bundleContext;

    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public <T> void registerModel(T o, Dictionary<String, ?> props) {
        log.info("\u001B[33mRegistering model: {}\u001B[0m ", o);
        ServiceRegistration<?> modelServiceRegistration =
                bundleContext.registerService((Class<T>) o.getClass(), o, props);
        serviceRegistrationMap.put(o, modelServiceRegistration);
    }

    public <T> void unregisterModel(T o) {
        log.info("\u001B[33mUnregistering model: {}\u001B[0m", o);
        checkState(serviceRegistrationMap.containsKey(o), "The model is not registered");
        serviceRegistrationMap.get(o).unregister();
    }

    public void registerTrace(TransformationTrace trace) {
        log.info("\u001B[33mRegistering trace model: {}\u001B[0m", trace);
        ServiceRegistration<?> modelServiceRegistration =
                bundleContext.registerService(TransformationTrace.class, trace, new Hashtable<>());
        serviceRegistrationMap.put(trace, modelServiceRegistration);
    }

    public void unregisterTrace(TransformationTrace trace) {
        log.info("\u001B[33mUnregistering trace model: {}\u001B[0m", trace);
        checkState(serviceRegistrationMap.containsKey(trace), "The trace model is not registered");
        serviceRegistrationMap.get(trace).unregister();
    }

    public void registerInputStreamAsBundle(InputStream is) throws BundleException, IOException {
        log.info("\u001B[33mInstalling stream as bundle: {}\u001B[0m" , is.toString()+ " - " + is.available());
        Bundle installedBundle = bundleContext.installBundle(this.getClass().getName() + is.toString(), is);
        installedBundle.start();
        bundleRegistrationMap.put(is, installedBundle);
    }

    public void ungisterInputStream(InputStream is) throws BundleException {
        log.info("\u001B[33mUnregistering trace model: {}\u001B[0m", is);
        checkState(bundleRegistrationMap.containsKey(is), "The input stream is not registered");
        bundleRegistrationMap.get(is).uninstall();
    }

    @Override
    public abstract void registerTramsformationContext(TransformationContext transformationContext, String sqlDialect);

    @Override
    public abstract void unregisterTramsformationContext(TransformationContext transformationContext, String sqlDialect);
}
