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

/**
 * Exception thrown when trace loading fails.
 *
 * <p>This exception indicates errors during trace file parsing or element resolution,
 * including malformed files and unresolved element references.</p>
 */
public class TraceLoadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TraceLoadException(String message) {
        super(message);
    }

    public TraceLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public TraceLoadException(Throwable cause) {
        super(cause);
    }
}
