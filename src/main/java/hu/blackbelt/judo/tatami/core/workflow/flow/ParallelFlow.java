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


import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parallel flow executes a set of works in parallel.
 *
 * The status of a parallel flow execution is defined as:
 *
 * <ul>
 *     <li>{@link hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus#COMPLETED}: If all works have successfully completed</li>
 *     <li>{@link hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus#FAILED}: If one of the works has failed</li>
 * </ul>
 *
 */
@Slf4j
public class ParallelFlow extends AbstractWorkFlow {

    private List<Work> works = new ArrayList<>();
    private ParallelFlowExecutor workExecutor;

    ParallelFlow(String name, List<Work> works, ParallelFlowExecutor parallelFlowExecutor) {
        super(name);
        this.works.addAll(works);
        this.workExecutor = parallelFlowExecutor;
    }

    /**
     * {@inheritDoc}
     */
    public ParallelFlowReport call() {
        log.debug("Call work '{}' - Call work:  '{}' ", new String[] {getName(),
                works.stream().map(w -> w.getName()).collect(Collectors.joining(", "))});

        ParallelFlowReport workFlowReport = new ParallelFlowReport();
        List<WorkReport> workReports = workExecutor.executeInParallel(works);
        workFlowReport.addAll(workReports);
        log.debug("Work {} Returns: {} ", getName(), workReports.stream().map(wr -> wr.toString()).collect(Collectors.joining(", ")));
        return workFlowReport;
    }

    public static class Builder {

        private String name;
        private List<Work> works;

        private Builder() {
            this.name = UUID.randomUUID().toString();
            this.works = new ArrayList<>();
        }

        public static ParallelFlow.Builder aNewParallelFlow() {
            return new ParallelFlow.Builder();
        }

        public ParallelFlow.Builder named(String name) {
            this.name = name;
            return this;
        }

        public ParallelFlow.Builder execute(Work... works) {
            this.works.addAll(Arrays.asList(works));
            return this;
        }

        public ParallelFlow.Builder execute(Optional<Work>... works) {
            this.works.addAll(Arrays.stream(works).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
            return this;
        }

        public ParallelFlow.Builder execute(Stream<Optional<Work>> works) {
            this.works.addAll(works.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
            return this;
        }

        public ParallelFlow build() {
            return new ParallelFlow(name, works, new ParallelFlowExecutor());
        }
    }
}
