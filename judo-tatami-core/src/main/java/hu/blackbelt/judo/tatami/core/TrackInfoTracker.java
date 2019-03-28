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
public class TrackInfoTracker extends ServiceTracker<TrackInfo, TrackInfo> {

    private TrackInfoService trackInfoService;
    private final Map<ServiceReference, TrackInfo> instanceCache = Maps.newConcurrentMap();

    TrackInfoTracker(TrackInfoService trackInfoService, BundleContext bundleContext, Class<TrackInfo> clazz) throws InvalidSyntaxException {
        super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
        this.trackInfoService = trackInfoService;
    }

    @Override
    public TrackInfo addingService(ServiceReference<TrackInfo> serviceReference) {
        if (serviceReference.isAssignableTo(super.context.getBundle(), TrackInfo.class.getName())) {
            TrackInfo instance = super.addingService(serviceReference);
            instanceCache.put(serviceReference, instance);
            log.info("bind TrackInfo type: {} name: {} ", instance.getTrackInfoName(), instance.getModelName());

            trackInfoService.add(instance);
            return instance;
        }
        return null;
    }

    @Override
    public void removedService(ServiceReference<TrackInfo> serviceReference, TrackInfo service) {
        TrackInfo instance = instanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        instanceCache.remove(serviceReference);
        log.info("unbind TrackInfo type: {} name: {} ", instance.getTrackInfoName(), instance.getModelName());

        trackInfoService.remove(instance);
        super.removedService(serviceReference, service);
    }

    @Override
    public void modifiedService(ServiceReference<TrackInfo> serviceReference,
                                TrackInfo service) {
        super.modifiedService(serviceReference, service);
    }

}
