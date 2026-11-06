package hu.blackbelt.judo.tatami.core;

import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import java.util.Map;

/**
 * OSGi (Karaf) source of the container-wide transformation mode: reads {@link TransformationMode#CONFIG_KEY} from the
 * optional configuration {@link TransformationMode#CONFIG_PID} (e.g. {@code etc/hu.blackbelt.judo.tatami.transformation.cfg}
 * with {@code mode = ETL}) and installs it via {@link TransformationMode#setConfiguredDefault}. The system property
 * {@value TransformationMode#SYSTEM_PROPERTY} still wins. Without configuration the default stays ZETA. ETL needs the
 * matching {@code -etl} bundles (Karaf feature {@code judo-platform-etl}). See change: split-etl-artifacts.
 */
@Component(immediate = true, service = TransformationModeConfiguration.class,
        configurationPid = TransformationMode.CONFIG_PID, configurationPolicy = ConfigurationPolicy.OPTIONAL)
@Slf4j
public class TransformationModeConfiguration {

    @Activate
    @Modified
    public void apply(Map<String, Object> properties) {
        Object raw = properties == null ? null : properties.get(TransformationMode.CONFIG_KEY);
        String value = raw == null ? null : raw.toString();
        TransformationMode mode = TransformationMode.parse(value).orElse(null);
        if (value != null && !value.trim().isEmpty() && mode == null) {
            log.warn("Ignoring invalid {} {}={} (expected ETL, ZETA or DUAL); using the default",
                    TransformationMode.CONFIG_PID, TransformationMode.CONFIG_KEY, value);
        }
        TransformationMode.setConfiguredDefault(mode);
        log.info("Transformation mode default: {} (effective now: {})",
                mode == null ? "unset" : mode, TransformationMode.fromSystemProperty());
    }

    @Deactivate
    public void deactivate() {
        TransformationMode.setConfiguredDefault(null);
    }
}
