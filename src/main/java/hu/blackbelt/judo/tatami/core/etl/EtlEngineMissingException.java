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

/**
 * Thrown when a stage runs in {@code ETL} or {@code DUAL} mode but no ETL engine for that stage
 * is available. The message names the stage and the add-on artifact that provides the engine.
 */
public class EtlEngineMissingException extends IllegalStateException {

    private final Class<?> engineType;
    private final String artifactCoordinates;

    public EtlEngineMissingException(Class<?> engineType, String artifactCoordinates) {
        super("No ETL engine found for stage '" + engineType.getName() + "'. "
                + "ETL is an optional add-on: add the artifact " + artifactCoordinates
                + " (and its Epsilon dependencies) to the classpath, or install its bundle on Karaf, "
                + "or run the stage in ZETA mode.");
        this.engineType = engineType;
        this.artifactCoordinates = artifactCoordinates;
    }

    public Class<?> getEngineType() {
        return engineType;
    }

    public String getArtifactCoordinates() {
        return artifactCoordinates;
    }
}
