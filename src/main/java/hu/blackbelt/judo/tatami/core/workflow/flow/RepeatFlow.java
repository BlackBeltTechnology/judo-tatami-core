package hu.blackbelt.judo.tatami.core.workflow.flow;

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

import hu.blackbelt.judo.tatami.core.workflow.work.NoOpWork;
import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReportPredicate;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A repeat flow executes a work repeatedly until its report is satisfied by a given predicate.
 */
@Slf4j
public class RepeatFlow extends AbstractWorkFlow {

    private Work work;
    private WorkReportPredicate predicate;

    RepeatFlow(String name, Work work, WorkReportPredicate predicate) {
        super(name);
        this.work = work;
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    public WorkReport call() {
        WorkReport workReport;
        log.debug("Call work '{}' - Call work:  '{}'", new String[] {getName(), work.getName()});
        do {
            workReport = work.call();

            if (workReport != null && WorkReportPredicate.FAILED.apply(workReport)) {
                log.error(String.format("Work '%s' has failed", work.getName(), workReport.getError()));
            }

        } while (predicate.apply(workReport));
        log.debug("Work {} Returns: {} ", getName(), workReport);
        return workReport;
    }

    public static class Builder {

        private String name;
        private Work work;
        private WorkReportPredicate predicate;

        private Builder() {
            this.name = UUID.randomUUID().toString();
            this.work = new NoOpWork();
            this.predicate = WorkReportPredicate.ALWAYS_FALSE;
        }

        public static RepeatFlow.Builder aNewRepeatFlow() {
            return new RepeatFlow.Builder();
        }

        public RepeatFlow.Builder named(String name) {
            this.name = name;
            return this;
        }

        public RepeatFlow.Builder repeat(Work work) {
            this.work = work;
            return this;
        }

        public RepeatFlow.Builder times(int times) {
            return until(WorkReportPredicate.TimesPredicate.times(times));
        }

        public RepeatFlow.Builder until(WorkReportPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public RepeatFlow build() {
            return new RepeatFlow(name, work, predicate);
        }
    }
}
