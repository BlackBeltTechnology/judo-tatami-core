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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EtlEngineRegistryTest {

    /** Has a provider in src/test/resources/META-INF/services. */
    public interface ServiceLoadedEngine {
        String run();
    }

    public static class ServiceLoadedEngineImpl implements ServiceLoadedEngine {
        @Override
        public String run() {
            return "service-loaded";
        }
    }

    /** Has no provider anywhere. */
    public interface UnprovidedEngine {
        String run();
    }

    @AfterEach
    void cleanUp() {
        EtlEngineRegistry.unregister(UnprovidedEngine.class, LATE);
    }

    private static final UnprovidedEngine LATE = () -> "late";

    @Test
    void findsEngineThroughServiceLoader() {
        ServiceLoadedEngine engine = EtlEngineRegistry.require(ServiceLoadedEngine.class, "g:a-etl");
        assertInstanceOf(ServiceLoadedEngineImpl.class, engine);
        assertEquals("service-loaded", engine.run());
    }

    @Test
    void missingEngineNamesStageAndArtifact() {
        EtlEngineMissingException e = assertThrows(EtlEngineMissingException.class,
                () -> EtlEngineRegistry.require(UnprovidedEngine.class, "hu.blackbelt.judo.tatami:judo-tatami-x-etl"));
        assertTrue(e.getMessage().contains(UnprovidedEngine.class.getName()), e.getMessage());
        assertTrue(e.getMessage().contains("hu.blackbelt.judo.tatami:judo-tatami-x-etl"), e.getMessage());
        assertEquals(UnprovidedEngine.class, e.getEngineType());
    }

    @Test
    void registrationAfterMissIsSeenBecauseMissesAreNotCached() {
        assertFalse(EtlEngineRegistry.find(UnprovidedEngine.class).isPresent());
        EtlEngineRegistry.register(UnprovidedEngine.class, LATE);
        assertSame(LATE, EtlEngineRegistry.require(UnprovidedEngine.class, "g:a"));
    }

    @Test
    void unregisterRemovesOnlyTheRegisteredInstance() {
        EtlEngineRegistry.register(UnprovidedEngine.class, LATE);
        EtlEngineRegistry.unregister(UnprovidedEngine.class, () -> "other");
        assertSame(LATE, EtlEngineRegistry.require(UnprovidedEngine.class, "g:a"));
        EtlEngineRegistry.unregister(UnprovidedEngine.class, LATE);
        assertFalse(EtlEngineRegistry.find(UnprovidedEngine.class).isPresent());
    }
}
