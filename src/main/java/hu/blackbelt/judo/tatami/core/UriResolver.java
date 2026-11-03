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

import java.net.URI;

/**
 * Epsilon-free resolution of a relative resource name against a base URI, including opaque
 * {@code jar:} URIs that {@link URI#resolve(String)} leaves untouched.
 * Same behaviour as {@code org.eclipse.epsilon.common.util.UriUtil.resolve(String, URI)} for the
 * relative file names used by the transformations.
 */
public final class UriResolver {

    private static final String JAR_FILE = "jar:file:/";
    private static final String JAR = "jar:/";

    private UriResolver() {
    }

    public static URI resolve(String relative, URI base) {
        URI relativeUri = URI.create(relative);
        if (relativeUri.isAbsolute()) {
            return relativeUri;
        }
        String baseString = base.toString();
        if (baseString.startsWith(JAR_FILE)) {
            // make the jar URI hierarchical, resolve, then restore the scheme (as Epsilon's UriUtil does)
            String resolved = URI.create(baseString.replace(JAR_FILE, JAR)).resolve(relative).toString();
            return URI.create(resolved.replace(JAR, JAR_FILE));
        }
        if (base.isOpaque()) {
            return URI.create(baseString + relative);
        }
        return base.resolve(relative);
    }
}
