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

/**
 * Epsilon-free EMF resource-set helpers for Zeta-only code
 * (replaces {@code hu.blackbelt.epsilon.runtime.execution.EmfUtils}).
 */
public final class EmfPackages {

    private EmfPackages() {
    }

    /**
     * Registers the Ecore package in the resource set's package registry.
     * Same behaviour as Epsilon's {@code EmfUtils.addEmfPackagesToResourceSet}.
     */
    public static void addEmfPackagesToResourceSet(ResourceSet resourceSet) {
        synchronized (resourceSet) {
            resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        }
    }
}
