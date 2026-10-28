package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
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

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * Represents a single trace mapping entry supporting multiple sources and targets.
 *
 * <p>This class supports both legacy ETL traces (single source → multiple targets)
 * and Zeta traces (potentially multiple sources → multiple targets with metadata).</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * TraceEntry entry = TraceEntry.builder()
 *     .source(sourceObject)
 *     .target(targetObject1)
 *     .target(targetObject2)
 *     .ruleName("Entity2Table")
 *     .primary(true)
 *     .build();
 * }</pre>
 */
@Builder
@Getter
public class TraceEntry {

    /**
     * Source elements of the transformation.
     * For ETL traces, this typically contains a single source.
     * For Zeta traces, this may contain multiple sources.
     */
    @Singular
    private final List<EObject> sources;

    /**
     * Target elements produced by the transformation.
     */
    @Singular
    private final List<EObject> targets;

    /**
     * Name of the transformation rule that created this mapping.
     * May be null for legacy ETL traces.
     */
    private final String ruleName;

    /**
     * Discriminator value for distinguishing multiple targets from the same rule.
     * Used in Zeta discriminated equivalence patterns.
     * May be null.
     */
    private final String discriminator;

    /**
     * Indicates whether this is a primary transformation result.
     * Primary results are returned by equivalent() calls.
     */
    private final boolean primary;

    /**
     * Returns the first source element, or null if no sources exist.
     * Convenience method for single-source scenarios.
     *
     * @return the first source EObject, or null
     */
    public EObject getSource() {
        return sources == null || sources.isEmpty() ? null : sources.get(0);
    }

    /**
     * Returns the first target element, or null if no targets exist.
     * Convenience method for single-target scenarios.
     *
     * @return the first target EObject, or null
     */
    public EObject getTarget() {
        return targets == null || targets.isEmpty() ? null : targets.get(0);
    }

    /**
     * Returns true if this entry has multiple sources.
     *
     * @return true if sources contains more than one element
     */
    public boolean hasMultipleSources() {
        return sources != null && sources.size() > 1;
    }

    /**
     * Returns true if this entry has multiple targets.
     *
     * @return true if targets contains more than one element
     */
    public boolean hasMultipleTargets() {
        return targets != null && targets.size() > 1;
    }
}
