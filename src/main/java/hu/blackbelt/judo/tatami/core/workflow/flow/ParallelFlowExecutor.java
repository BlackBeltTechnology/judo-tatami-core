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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
class ParallelFlowExecutor {

    private static final Logger LOGGER = Logger.getLogger(ParallelFlowExecutor.class.getName());

    /*
     * TODO Making the executor configurable requires to answer the following questions first:
     *
     * 1. If the user provides a custom executor, when should it be shutdown? -> Could be documented so the user shuts it down himself
     * 2. If the user provides a custom executor which is shared by multiple parallel flow, shutting it down here (as currently done) may impact other flows
     * 3. If it is decided to shut down the executor at the end of the parallel flow, the parallel flow could not be re-run (in a repeat flow for example) since the executor will be in an illegal state
     */
    private ExecutorService workExecutor;

    ParallelFlowExecutor() {
        this.workExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());;
    }

    List<WorkReport> executeInParallel(List<Work> works) {
        // re-init in case it has been shut down in a previous run (See question 3)
        if(workExecutor.isShutdown()) {
            workExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        }

        // submit works to be executed in parallel
        Map<Work, Future<WorkReport>> reportFutures = new HashMap<>();
        for (Work work : works) {
            Future<WorkReport> reportFuture = workExecutor.submit(work);
            reportFutures.put(work, reportFuture);
        }

        // poll for work completion
        int finishedWorks = works.size();
        // FIXME polling futures for completion, not sure this is the best way to run callables in parallel and wait them for completion (use CompletionService??)
        while (finishedWorks > 0) {
            for (Future<WorkReport> future : reportFutures.values()) {
                if (future != null && future.isDone()) {
                        finishedWorks--;
                }
            }
        }

        // gather reports
        List<WorkReport> workReports = new ArrayList<>();
        for (Map.Entry<Work, Future<WorkReport>> entry : reportFutures.entrySet()) {
            try {
                workReports.add(entry.getValue().get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(String.format("Unable to get work report of work '%s'", entry.getKey().getName()), e);
            }
        }

        workExecutor.shutdown(); // because if not, the workflow engine may run forever.. (See question 2).
        return workReports;
    }
}
