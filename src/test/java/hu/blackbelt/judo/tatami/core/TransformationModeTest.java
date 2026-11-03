package hu.blackbelt.judo.tatami.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Precedence of {@link TransformationMode#fromSystemProperty()}: property, then PID config, then ZETA. */
class TransformationModeTest {

    private final String previous = System.getProperty(TransformationMode.SYSTEM_PROPERTY);
    private final TransformationModeConfiguration configuration = new TransformationModeConfiguration();

    @AfterEach
    void restore() {
        configuration.deactivate();
        if (previous == null) {
            System.clearProperty(TransformationMode.SYSTEM_PROPERTY);
        } else {
            System.setProperty(TransformationMode.SYSTEM_PROPERTY, previous);
        }
    }

    @Test
    void unsetIsZeta() {
        System.clearProperty(TransformationMode.SYSTEM_PROPERTY);
        assertEquals(TransformationMode.ZETA, TransformationMode.fromSystemProperty());
    }

    @Test
    void invalidPropertyIsZeta() {
        System.setProperty(TransformationMode.SYSTEM_PROPERTY, "nope");
        assertEquals(TransformationMode.ZETA, TransformationMode.fromSystemProperty());
    }

    @Test
    void propertyIsCaseInsensitive() {
        System.setProperty(TransformationMode.SYSTEM_PROPERTY, " etl ");
        assertEquals(TransformationMode.ETL, TransformationMode.fromSystemProperty());
    }

    @Test
    void pidConfigAppliesWhenPropertyUnset() {
        System.clearProperty(TransformationMode.SYSTEM_PROPERTY);
        configuration.apply(Map.of(TransformationMode.CONFIG_KEY, "ETL"));
        assertEquals(TransformationMode.ETL, TransformationMode.fromSystemProperty());
    }

    @Test
    void propertyWinsOverPidConfig() {
        configuration.apply(Map.of(TransformationMode.CONFIG_KEY, "ETL"));
        System.setProperty(TransformationMode.SYSTEM_PROPERTY, "ZETA");
        assertEquals(TransformationMode.ZETA, TransformationMode.fromSystemProperty());
    }

    @Test
    void invalidPidValueAndDeactivateFallBackToZeta() {
        System.clearProperty(TransformationMode.SYSTEM_PROPERTY);
        configuration.apply(Map.of(TransformationMode.CONFIG_KEY, "ETL"));
        configuration.apply(Map.of(TransformationMode.CONFIG_KEY, "bogus"));
        assertEquals(TransformationMode.ZETA, TransformationMode.fromSystemProperty());
        configuration.apply(Map.of(TransformationMode.CONFIG_KEY, "DUAL"));
        configuration.deactivate();
        assertEquals(TransformationMode.ZETA, TransformationMode.fromSystemProperty());
    }
}
