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

/**
 * Default implementation of {@link WorkReport}.
 */
public class DefaultWorkReport implements WorkReport {

    private WorkStatus status;
    private Throwable error;

    /**
     * Create a new {@link DefaultWorkReport}.
     *
     * @param status of work
     */
    public DefaultWorkReport(WorkStatus status) {
        this.status = status;
    }

    /**
     * Create a new {@link DefaultWorkReport}.
     *
     * @param status of work
     * @param error if any
     */
    public DefaultWorkReport(WorkStatus status, Throwable error) {
        this(status);
        this.error = error;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "DefaultWorkReport {" +
                "status=" + status +
                ", error=" + (error == null ? "''" : error) +
                '}';
    }
}
