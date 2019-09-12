package hu.blackbelt.judo.tatami.workflow.osgi;

import com.google.common.collect.Maps;
import hu.blackbelt.osgi.utils.osgi.api.BundleCallback;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationConstants.BUNDLE_SYMBOLIC_NAME;
import static hu.blackbelt.judo.tatami.workflow.osgi.TransformationConstants.TRANSFORMATION_NAME;

/**
 * Helper class for transformation budle tracking.
 */
public class TransformationBundleTrackerHelper{

    public static final String HEADER_VALUE_SEPARATOR  = ",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    public static final String HEADER_PARAM_SEPARATOR  = ";(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    public static final String HEADER_KEYVAL_SEPARATOR = "=(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

    public static void registerTranformationBundleTracker(Object key,
                                                          BundleTrackerManager bundleTrackerManager,
                                                          BundleContext bundleContext,
                                                          Map<String, ServiceRegistration<TransformationBundleHolder>> transformationBundleHolders,
                                                          Set<String> trackedBundleSymbolicNames) {

        bundleTrackerManager.registerBundleCallback(key,
                // Register callback
                new BundleCallback() {
                    @Override
                    public void accept(Bundle bundle) {
                        TransformationBundleHolder transformationBundleHolder = new TransformationBundleHolder(bundle);
                        transformationBundleHolders.put(bundle.getSymbolicName(),
                                bundleContext.registerService(TransformationBundleHolder.class, transformationBundleHolder, bundle.getHeaders()));
                    }

                    @Override
                    public Thread process(Bundle bundle) {
                        return null;
                    }
                },
                // Unregister callback
                new BundleCallback() {
                    @Override
                    public void accept(Bundle bundle) {
                        transformationBundleHolders.get(bundle.getSymbolicName()).unregister();
                    }

                    @Override
                    public Thread process(Bundle bundle) {
                        return null;
                    }
                },
                b -> trackedBundleSymbolicNames.contains(b.getSymbolicName())
        );

    }

    public static Map<String, Map<String, String>> parseTransformationBundleConfigurations(String value) {
        Map<String, Map<String, String>> headerEntries = Maps.newHashMap();
        for (String headerKeyValue : value.split(HEADER_VALUE_SEPARATOR)) {
            Map<String, String> entry = new HashMap<>();
            if (headerKeyValue != null && !"".equals(headerKeyValue.trim())) {
                for (String keyVal : headerKeyValue.split(HEADER_PARAM_SEPARATOR)) {
                    if (keyVal != null && !"".equals(keyVal.trim())) {
                        String[] keyAndVal = keyVal.split(HEADER_KEYVAL_SEPARATOR);
                        if (keyAndVal.length != 2) {
                            throw new IllegalArgumentException("Configuration have to be in the following format: key1_1=val1_1;key1_2=val2_1,key2_1=val2_1;key2_1=val2_2");
                        }
                        entry.put(keyAndVal[0].trim(), keyAndVal[1].trim());
                    }
                }
                checkArgument(entry.containsKey(TRANSFORMATION_NAME), "'Name' key is required in transformation bundle definition");
                checkArgument(entry.containsKey(BUNDLE_SYMBOLIC_NAME), "'Name' key is required in transformation bundle definition");

                headerEntries.put(entry.get(TRANSFORMATION_NAME), entry);
            }
        }
        return headerEntries;
    }
}