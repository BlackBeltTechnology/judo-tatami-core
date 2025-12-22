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
    ZETA;

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
     * Gets the transformation mode from system property, or returns the default.
     * <p>
     * This method reads the system property {@value #SYSTEM_PROPERTY} and
     * returns the corresponding mode. If the property is not set or contains
     * an invalid value, {@link #DEFAULT} (ZETA) is returned.
     * </p>
     *
     * @return the transformation mode from system property, or DEFAULT
     */
    public static TransformationMode fromSystemProperty() {
        String value = System.getProperty(SYSTEM_PROPERTY);
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT;
        }
        try {
            return TransformationMode.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }

    /**
     * Checks if this mode uses the Zeta (Java-based) transformation engine.
     *
     * @return true if this is ZETA mode
     */
    public boolean isZeta() {
        return this == ZETA;
    }

    /**
     * Checks if this mode uses the ETL (Epsilon) transformation engine.
     *
     * @return true if this is ETL mode
     */
    public boolean isEtl() {
        return this == ETL;
    }
}
