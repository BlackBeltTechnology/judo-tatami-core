package hu.blackbelt.judo.tatami.psm2asm.osgi;

import hu.blackbelt.osgi.utils.osgi.api.BundleCallback;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import hu.blackbelt.osgi.utils.osgi.api.BundleUtil;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component(immediate = true)
@Slf4j
public class Psm2AsmTransformationTraceModelBundleTracker {
    public static final String PSM2ASM_TRACE = "Psm2Asm-Trace";
    public static final String NAME = "name";

    @Reference
    BundleTrackerManager bundleTrackerManager;

    @Reference
    ConfigurationAdmin configurationAdmin;

    @Activate
    public void activate(final ComponentContext componentContext) {
        bundleTrackerManager.registerBundleCallback(this.getClass().getName(),
                new TraceBundleRegisterCallback(componentContext.getBundleContext()),
                new TraceBundleUnregisterCallback(),
                new TraceBundlePredicate());
    }

    @Deactivate
    public void deactivate(final ComponentContext componentContext) {
        bundleTrackerManager.unregisterBundleCallback(this.getClass().getName());
    }

    private static class TraceBundlePredicate implements Predicate<Bundle> {
        @Override
        public boolean test(Bundle trackedBundle) {
            return BundleUtil.hasHeader(trackedBundle, PSM2ASM_TRACE);
        }
    }

    private class TraceBundleRegisterCallback implements BundleCallback {

        BundleContext bundleContext;

        public TraceBundleRegisterCallback(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }


        @Override
        public void accept(Bundle trackedBundle) {
            List<Map<String, String>> entries = BundleUtil.getHeaderEntries(trackedBundle, PSM2ASM_TRACE);

            for (Map<String, String> params : entries) {
                String key = params.get(NAME);
                try {
                    Configuration config = getConfiguration(Psm2AsmTransformationTraceRegistration.class.getName(), key);
                    if (config.getBundleLocation() != null) {
                        config.setBundleLocation(null);
                    }
                    if (config.getProperties() == null
                            || config.getProperties().get(this.getClass().getName()) == null
                            || !config.getProperties().get(this.getClass().getName())
                                    .equals(getPidName(Psm2AsmTransformationTraceRegistration.class.getName(), key))) {
                        log.info("Registering Trace model registration service: " + key);
                        config.update(Psm2AsmTransformationTraceRegistration
                                .toServiceParameters(key, trackedBundle.getEntry(params.get("file")).openStream()));
                    }
                } catch (Exception e) {
                    log.error("Could not create trace registration: " + key);
                }
            }
        }

        @Override
        public Thread process(Bundle bundle) {
            return null;
        }
    }

    private class TraceBundleUnregisterCallback implements BundleCallback {
        @Override
        public void accept(Bundle trackedBundle) {
            List<Map<String, String>> entries = BundleUtil.getHeaderEntries(trackedBundle, PSM2ASM_TRACE);
            for (Map<String, String> params : entries) {
                String key = params.get(NAME);
                try {
                    deleteConfig(Psm2AsmTransformationTraceRegistration.class.getName(), key);
                } catch (Exception e) {
                    log.error("Could not delete trace registration: " + key);
                }
            }
        }

        @Override
        public Thread process(Bundle bundle) {
            return null;
        }
    }

    private boolean deleteConfig(String pid, String factoryPid) throws Exception {
        Configuration config = getConfiguration(pid, factoryPid);
        config.delete();
        return true;
    }

    private Configuration getConfiguration(String pid, String factoryPid) throws Exception {
        Configuration oldConfiguration = findExistingConfiguration(pid, factoryPid);
        if (oldConfiguration != null) {
            return oldConfiguration;
        } else {
            Configuration newConfiguration;
            if (factoryPid != null) {
                newConfiguration = configurationAdmin.createFactoryConfiguration(pid, null);
            } else {
                newConfiguration = configurationAdmin.getConfiguration(pid, null);
            }
            return newConfiguration;
        }
    }

    private Configuration findExistingConfiguration(String pid, String factoryPid) throws Exception {
        String filter = "(" + this.getClass().getName() + "=" + getPidName(pid, factoryPid) + ")";
        Configuration[] configurations = configurationAdmin.listConfigurations(filter);
        if (configurations != null && configurations.length > 0) {
            return configurations[0];
        } else {
            return null;
        }
    }

    private String getPidName(String pid, String factoryPid) {
        String suffix = factoryPid == null ? "" : "-" + factoryPid;
        return pid + suffix;
    }
}
