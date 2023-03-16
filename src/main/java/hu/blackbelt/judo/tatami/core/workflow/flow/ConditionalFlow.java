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

/**
 * A conditional flow is defined by 4 artifacts:
 *
 * <ul>
 *     <li>The work to execute first</li>
 *     <li>A predicate for the conditional logic</li>
 *     <li>The work to execute if the predicate is satisfied</li>
 *     <li>The work to execute if the predicate is not satisfied (optional)</li>
 * </ul>
 *
 * @see ConditionalFlow.Builder
 *
 */
@Slf4j
public class ConditionalFlow extends AbstractWorkFlow {

    private Work toExecute, nextOnPredicateSuccess, nextOnPredicateFailure;
    private WorkReportPredicate predicate;

    ConditionalFlow(String name, Work toExecute, Work nextOnPredicateSuccess, Work nextOnPredicateFailure, WorkReportPredicate predicate) {
        super(name);
        this.toExecute = toExecute;
        this.nextOnPredicateSuccess = nextOnPredicateSuccess;
        this.nextOnPredicateFailure = nextOnPredicateFailure;
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    public WorkReport call() {
        log.debug("Call work '{}' - Call work: '{}'", new String[] {getName(), toExecute.getName()});
        WorkReport jobReport = toExecute.call();

        if (predicate.apply(jobReport)) {
            jobReport = nextOnPredicateSuccess.call();
        } else {
            log.debug("Call work {} - Predicate {} unmatched, and  '{}' FAILED", new String[] {
                    getName(), jobReport == null ? "" :
                    (jobReport.getStatus() == null ? "" : jobReport.getStatus().name())
            });
            if (nextOnPredicateFailure != null && !(nextOnPredicateFailure instanceof NoOpWork)) { // else is optional
                log.debug("Call work {} - Predicate {} unmatched, and  '{}' - Call work: {}", new String[] {
                        getName(), jobReport == null ? "" :
                        (jobReport.getStatus() == null ? "" : jobReport.getStatus().name()),
                        toExecute.getName(), nextOnPredicateFailure.getName()
                });
                jobReport = nextOnPredicateFailure.call();
            }
        }
        log.debug("Work {} Returns: {} ", getName(), jobReport);

        if (jobReport != null && WorkReportPredicate.FAILED.apply(jobReport)) {
            log.error(String.format("Work '%s' has failed", toExecute.getName(), jobReport.getError()));
        }

        return jobReport;
    }

    public static class Builder {

        private String name;
        private Work toExecute, nextOnPredicateSuccess, nextOnPredicateFailure;
        private WorkReportPredicate predicate;

        private Builder() {
            this.name = UUID.randomUUID().toString();
            this.toExecute = new NoOpWork();
            this.nextOnPredicateSuccess = new NoOpWork();
            this.nextOnPredicateFailure = new NoOpWork();
            this.predicate = WorkReportPredicate.ALWAYS_FALSE;
        }

        public static ConditionalFlow.Builder aNewConditionalFlow() {
            return new ConditionalFlow.Builder();
        }

        public ConditionalFlow.Builder named(String name) {
            this.name = name;
            return this;
        }

        public ConditionalFlow.Builder execute(Work work) {
            this.toExecute = work;
            return this;
        }

        public ConditionalFlow.Builder when(WorkReportPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public ConditionalFlow.Builder then(Work work) {
            this.nextOnPredicateSuccess = work;
            return this;
        }

        public ConditionalFlow.Builder otherwise(Work work) {
            this.nextOnPredicateFailure = work;
            return this;
        }

        public ConditionalFlow build() {
            return new ConditionalFlow(name, toExecute, nextOnPredicateSuccess, nextOnPredicateFailure, predicate);
        }
    }
}
