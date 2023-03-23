package hu.blackbelt.judo.tatami.core.workflow.work;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
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

import java.util.concurrent.Callable;

/**
 * Implementations of this interface must:
 * <ul>
 *     <li>catch exceptions and return {@link WorkStatus#FAILED}</li>
 *     <li>make sure the work in finished in a finite amount of time</li>
 * </ul>
 *
 * Work name must be unique within a workflow.
 */
public interface Work extends Callable<WorkReport> {

    String getName();

    WorkReport call();
}
