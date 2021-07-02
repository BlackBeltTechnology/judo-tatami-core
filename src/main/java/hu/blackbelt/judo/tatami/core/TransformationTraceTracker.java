package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Map;

@Slf4j
public class TransformationTraceTracker extends ServiceTracker<TransformationTrace, TransformationTrace> {

    private TransformationTraceService transformationTraceService;
    private final Map<ServiceReference, TransformationTrace> instanceCache = Maps.newConcurrentMap();

    TransformationTraceTracker(TransformationTraceService transformationTraceService, BundleContext bundleContext, Class<TransformationTrace> clazz) throws InvalidSyntaxException {
        super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
        this.transformationTraceService = transformationTraceService;
    }

    @Override
    public TransformationTrace addingService(ServiceReference<TransformationTrace> serviceReference) {
        if (serviceReference.isAssignableTo(super.context.getBundle(), TransformationTrace.class.getName())) {
            TransformationTrace instance = super.addingService(serviceReference);
            instanceCache.put(serviceReference, instance);
            log.info("bind TransformationTrace type: {} name: {} ", instance.getTransformationTraceName(), instance.getModelName());

            transformationTraceService.add(instance);
            return instance;
        }
        return null;
    }

    @Override
    public void removedService(ServiceReference<TransformationTrace> serviceReference, TransformationTrace service) {
        TransformationTrace instance = instanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        instanceCache.remove(serviceReference);
        log.info("unbind TransformationTrace type: {} name: {} ", instance.getTransformationTraceName(), instance.getModelName());

        transformationTraceService.remove(instance);
        super.removedService(serviceReference, service);
    }

    @Override
    public void modifiedService(ServiceReference<TransformationTrace> serviceReference,
                                TransformationTrace service) {
        super.modifiedService(serviceReference, service);
    }

}
