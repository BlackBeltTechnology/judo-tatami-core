package hu.blackbelt.judo.tatami.core.etl;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Locates the optional ETL engine of a transformation stage.
 *
 * <p>Each dual-mode stage defines its engine contract as an interface in its Zeta-only core
 * artifact (for example {@code Psm2AsmEtlEngine}); the stage's {@code -etl} add-on artifact
 * implements it. Core code asks for an engine only in {@code ETL}/{@code DUAL} mode, so a
 * {@code ZETA} run never touches an add-on class, and therefore never an Epsilon class.</p>
 *
 * <p>Two discovery paths feed the same registry:</p>
 * <ul>
 *   <li>plain Java / Spring Boot: {@link ServiceLoader} via {@code META-INF/services/<engine interface>};</li>
 *   <li>Karaf/OSGi: the add-on bundle's Declarative Services component calls {@link #register} /
 *   {@link #unregister}.</li>
 * </ul>
 *
 * <p>Misses are never cached: an engine registered after a failed lookup is found by the next one
 * (OSGi start order is not guaranteed).</p>
 */
public final class EtlEngineRegistry {

    private static final Map<Class<?>, Object> REGISTERED = new ConcurrentHashMap<>();

    private EtlEngineRegistry() {
    }

    /** Registers an engine (OSGi activation or tests). Replaces any engine registered for the type. */
    public static <E> void register(Class<E> engineType, E engine) {
        REGISTERED.put(engineType, engineType.cast(engine));
    }

    /** Removes the engine only if it is the one currently registered for the type. */
    public static <E> void unregister(Class<E> engineType, E engine) {
        REGISTERED.remove(engineType, engine);
    }

    /** Explicitly registered engine first, then the first {@link ServiceLoader} provider. */
    public static <E> Optional<E> find(Class<E> engineType) {
        Object registered = REGISTERED.get(engineType);
        if (registered != null) {
            return Optional.of(engineType.cast(registered));
        }
        Optional<E> loaded = load(engineType, Thread.currentThread().getContextClassLoader());
        if (loaded.isEmpty() && engineType.getClassLoader() != null) {
            loaded = load(engineType, engineType.getClassLoader());
        }
        return loaded;
    }

    /**
     * Returns the engine for the stage.
     *
     * @param artifactCoordinates Maven coordinates of the add-on, reported when the engine is missing
     * @throws EtlEngineMissingException when no engine is available
     */
    public static <E> E require(Class<E> engineType, String artifactCoordinates) {
        return find(engineType).orElseThrow(() -> new EtlEngineMissingException(engineType, artifactCoordinates));
    }

    private static <E> Optional<E> load(Class<E> engineType, ClassLoader classLoader) {
        if (classLoader == null) {
            return Optional.empty();
        }
        Iterator<E> providers = ServiceLoader.load(engineType, classLoader).iterator();
        return providers.hasNext() ? Optional.of(providers.next()) : Optional.empty();
    }
}
