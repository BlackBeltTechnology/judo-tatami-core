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

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Expected values follow org.eclipse.epsilon.common.util.UriUtil.resolve semantics. */
class UriResolverTest {

    @Test
    void resolvesAgainstFileDirectory() {
        assertEquals(URI.create("file:/opt/x/tatami/asm2rdbms/model/mapping-hsqldb-rdbms.model"),
                UriResolver.resolve("mapping-hsqldb-rdbms.model", URI.create("file:/opt/x/tatami/asm2rdbms/model/")));
    }

    @Test
    void resolvesInsideJarFileUri() {
        assertEquals(URI.create("jar:file:/m2/a-1.0.jar!/tatami/asm2rdbms/model/RDBMS_Sql_Name_Mapping.csv"),
                UriResolver.resolve("RDBMS_Sql_Name_Mapping.csv",
                        URI.create("jar:file:/m2/a-1.0.jar!/tatami/asm2rdbms/model/")));
    }

    @Test
    void resolvesParentSegmentInsideJar() {
        assertEquals(URI.create("jar:file:/m2/a.jar!/tatami/operations/util.eol"),
                UriResolver.resolve("../operations/util.eol", URI.create("jar:file:/m2/a.jar!/tatami/transformations/")));
    }

    @Test
    void concatenatesOtherOpaqueUris() {
        assertEquals(URI.create("bundle:12!/tatami/model/x.csv"),
                UriResolver.resolve("x.csv", URI.create("bundle:12!/tatami/model/")));
    }

    @Test
    void absoluteRelativeWins() {
        assertEquals(URI.create("file:/other/x.csv"), UriResolver.resolve("file:/other/x.csv", URI.create("file:/a/")));
    }
}
