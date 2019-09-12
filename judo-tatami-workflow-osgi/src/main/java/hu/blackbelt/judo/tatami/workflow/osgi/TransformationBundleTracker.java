package hu.blackbelt.judo.tatami.workflow.osgi;

import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;
import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationBundleTrackerHelper.parseTransformationBundleConfigurations;
import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationBundleTrackerHelper.registerTranformationBundleTracker;
import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationConstants.BUNDLE_SYMBOLIC_NAME;
import static java.util.stream.Collectors.toSet;


/**
 * This classs tracks all the given transformation bundles defined in {@link TransformationBundleTrackerConfig}
 * and registering bundles as filterable services in OSGGi service registry via {@link TransformationBundleHolder}.
 * This tracker configurations contains which bundles are used for transformations. When the
 * default transformation have to be override, the tatami transformation can be replaced a customized one which
 * are on different bundle. On that case the coresponding bundle have to be defined in this tracker configuration.
 * This tracker bundles used by all of the transformation steps system wide.
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
@Designate(ocd = TransformationBundleTrackerConfig.class)
public class TransformationBundleTracker {

    @Reference
    BundleTrackerManager bundleTrackerManager;

    Map<String, ServiceRegistration<TransformationBundleHolder>> bunldleHolders = newConcurrentMap();
    Map<String, Map<String, String>> transformationBundleCondigurations = newConcurrentMap();

    @Activate
    public void activate(ComponentContext componentContextContext, TransformationBundleTrackerConfig config) {
        // Processing all bundles and tags that contains URI's of required script URI's.
        transformationBundleCondigurations = parseTransformationBundleConfigurations(config.transformationBundles());
        registerTranformationBundleTracker(this, bundleTrackerManager, componentContextContext.getBundleContext(), bunldleHolders,
                transformationBundleCondigurations.values().stream().map(v -> v.get(BUNDLE_SYMBOLIC_NAME)).collect(toSet()));
    }

    @Deactivate
    public void deactivate() {
        bundleTrackerManager.unregisterBundleCallback(this);
        bunldleHolders.values().stream().forEach(s -> s.unregister());
    }

}