package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * JUDO Tatami parent
 * %%
 * Copyright (C) 2018 - 2024 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

/**
 * Defines the transformation execution mode for model transformations.
 * <p>
 * This enum controls which transformation engine is used when executing
 * model transformations. By default, ZETA (Java-based) transformations
 * are used, but this can be overridden to use ETL for backward compatibility.
 * </p>
 *
 * <strong>Usage Examples:</strong>
 *
 * Default behavior (uses ZETA):
 * <pre>{@code
 * Psm2AsmWork work = new Psm2AsmWork(context);
 * work.execute(); // Uses ZETA by default
 * }</pre>
 *
 * Override to use ETL (legacy):
 * <pre>{@code
 * context.put(Psm2AsmWorkParameter.psm2AsmWorkParameter()
 *         .transformationMode(TransformationMode.ETL)
 *         .build());
 * }</pre>
 *
 * System property override:
 * <pre>{@code
 * // Set via -Djudo.transformation.mode=ETL
 * TransformationMode mode = TransformationMode.fromSystemProperty();
 * }</pre>
 */
public enum TransformationMode {

    /**
     * Use Epsilon ETL transformation engine (legacy).
     * <p>
     * This is the original implementation using Epsilon scripts (.etl files).
     * Use this mode for backward compatibility or when debugging transformation
     * differences.
     * </p>
     */
    ETL,

    /**
     * Use Zeta Java transformation engine.
     * This uses the Java-based Zeta framework for transformations, providing:
     * better IDE support (debugging, refactoring, navigation),
     * type safety with compile-time checking, improved performance,
     * and standard JUnit testing patterns.
     */
    ZETA,

    /**
     * Run both ETL and ZETA transformation engines.
     * <p>
     * This mode executes both transformation engines and compares their outputs
     * for validation purposes. Useful for verifying Zeta transformation correctness
     * against the reference ETL implementation during migration or testing.
     * </p>
     */
    DUAL;

    /**
     * System property name for overriding transformation mode.
     */
    public static final String SYSTEM_PROPERTY = "judo.transformation.mode";

    /**
     * The default transformation mode.
     * ZETA is the default. Use ETL for backward compatibility or when debugging
     * transformation differences.
     */
    public static final TransformationMode DEFAULT = ZETA;

    /**
     * Karaf / Config Admin PID that sets the mode for a container (key {@value #CONFIG_KEY}), e.g.
     * {@code etc/hu.blackbelt.judo.tatami.transformation.cfg} with {@code mode = ETL}. Read by
     * {@link TransformationModeConfiguration}. See change: split-etl-artifacts.
     */
    public static final String CONFIG_PID = "hu.blackbelt.judo.tatami.transformation";

    /** Configuration key of {@link #CONFIG_PID}. */
    public static final String CONFIG_KEY = "mode";

    /** Container-wide default set from {@link #CONFIG_PID}; {@code null} when not configured. */
    private static volatile TransformationMode configuredDefault;

    /**
     * Sets (or clears with {@code null}) the container-wide default used when {@value #SYSTEM_PROPERTY} is not set.
     * Called by {@link TransformationModeConfiguration} (OSGi); tests may call it directly.
     */
    public static void setConfiguredDefault(TransformationMode mode) {
        configuredDefault = mode;
    }

    /** The container-wide default from {@link #CONFIG_PID}, if configured. */
    public static java.util.Optional<TransformationMode> getConfiguredDefault() {
        return java.util.Optional.ofNullable(configuredDefault);
    }

    /**
     * Parses a mode name case-insensitively; empty for {@code null}, blank or unknown values.
     */
    public static java.util.Optional<TransformationMode> parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(TransformationMode.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return java.util.Optional.empty();
        }
    }

    /**
     * Resolves the effective mode. Precedence: the system property {@value #SYSTEM_PROPERTY} (when set to a valid
     * value), then the container default from {@link #CONFIG_PID} (Karaf), then {@link #DEFAULT} (ZETA). An invalid
     * property value is ignored, as if unset.
     *
     * @return the effective transformation mode
     */
    public static TransformationMode fromSystemProperty() {
        return parse(System.getProperty(SYSTEM_PROPERTY))
                .or(TransformationMode::getConfiguredDefault)
                .orElse(DEFAULT);
    }

    /**
     * Checks if this mode uses the Zeta (Java-based) transformation engine.
     *
     * @return true if this is ZETA or DUAL mode
     */
    public boolean isZeta() {
        return this == ZETA || this == DUAL;
    }

    /**
     * Checks if this mode uses the ETL (Epsilon) transformation engine.
     *
     * @return true if this is ETL or DUAL mode
     */
    public boolean isEtl() {
        return this == ETL || this == DUAL;
    }

    /**
     * Checks if this mode runs both transformation engines.
     *
     * @return true if this is DUAL mode
     */
    public boolean isDual() {
        return this == DUAL;
    }
}
