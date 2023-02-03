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

import hu.blackbelt.judo.tatami.core.workflow.work.DefaultWorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReportPredicate;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A sequential flow executes a set of works in sequence.
 *
 * If a work fails, next works in the pipeline will be skipped.
 *
 */
@Slf4j
public class SequentialFlow extends AbstractWorkFlow {

    private static final Logger LOGGER = Logger.getLogger(SequentialFlow.class.getName());

    private List<Work> works = new ArrayList<>();

    SequentialFlow(String name, List<Work> works) {
        super(name);
        this.works.addAll(works);
    }

    /**
     * {@inheritDoc}
     */
    public WorkReport call() {
        WorkReport workReport = null;

        log.debug("Call work '{}' - Call work:  '{}' ", new String[] {getName(),
                works.stream().map(w -> w.getName()).collect(Collectors.joining(", "))});

        if (works.size() == 0) {
            return new DefaultWorkReport(WorkStatus.COMPLETED);
        }
        for (Work work : works) {
            workReport = work.call();
            if (workReport != null && WorkReportPredicate.FAILED.apply(workReport)) {
                log.error(String.format("Work '%s' has failed, skipping subsequent works", work.getName(), workReport.getError()));
                break;
            }
        }
        log.debug("Work {} Returns: {} ", getName(), workReport);
        return workReport;
    }

    public static class Builder {

        private String name;
        private List<Work> works;

        private Builder() {
            this.name = UUID.randomUUID().toString();
            this.works = new ArrayList<>();
        }

        public static SequentialFlow.Builder aNewSequentialFlow() {
            return new SequentialFlow.Builder();
        }

        public SequentialFlow.Builder named(String name) {
            this.name = name;
            return this;
        }

        public SequentialFlow.Builder execute(Work work) {
            this.works.add(work);
            return this;
        }
        
        public SequentialFlow.Builder execute(Work... works) {
            this.works.addAll(Arrays.asList(works));
            return this;
        }

        public SequentialFlow.Builder execute(Optional<Work>... works) {
            this.works.addAll(Arrays.stream(works).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
            return this;
        }

        public SequentialFlow.Builder execute(Stream<Optional<Work>> works) {
            this.works.addAll(works.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
            return this;
        }

        public SequentialFlow.Builder then(Work work) {
            this.works.add(work);
            return this;
        }

        public SequentialFlow build() {
            return new SequentialFlow(name, works);
        }
    }
}
