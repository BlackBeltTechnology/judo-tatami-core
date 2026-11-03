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

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Parity with Epsilon's EmfUtils.addEmfPackagesToResourceSet, which (bytecode-verified,
 * epsilon-runtime-execution 2.8.0) registers exactly EcorePackage.eINSTANCE under
 * EcorePackage.eNS_URI in the resource set's local package registry.
 */
class EmfPackagesTest {

    @Test
    void registersEcorePackageInLocalRegistry() {
        ResourceSet resourceSet = new ResourceSetImpl();
        int before = resourceSet.getPackageRegistry().size();

        EmfPackages.addEmfPackagesToResourceSet(resourceSet);

        assertSame(EcorePackage.eINSTANCE, resourceSet.getPackageRegistry().get(EcorePackage.eNS_URI));
        assertEquals(before + 1, resourceSet.getPackageRegistry().size());
    }

    @Test
    void isIdempotent() {
        ResourceSet resourceSet = new ResourceSetImpl();
        EmfPackages.addEmfPackagesToResourceSet(resourceSet);
        int after = resourceSet.getPackageRegistry().size();
        EmfPackages.addEmfPackagesToResourceSet(resourceSet);
        assertEquals(after, resourceSet.getPackageRegistry().size());
    }
}
