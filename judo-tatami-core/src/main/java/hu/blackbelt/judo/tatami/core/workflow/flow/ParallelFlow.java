package hu.blackbelt.judo.tatami.core.workflow.flow;


import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        log.info("Call work '{}' - Call work:  '{}' ", new String[] {getName(),
                works.stream().map(w -> w.getName()).collect(Collectors.joining(", "))});

        ParallelFlowReport workFlowReport = new ParallelFlowReport();
        List<WorkReport> workReports = workExecutor.executeInParallel(works);
        workFlowReport.addAll(workReports);
        log.info("Work {} Returns: {} ", getName(), workReports.stream().map(wr -> wr.toString()).collect(Collectors.joining(", ")));
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

        public ParallelFlow build() {
            return new ParallelFlow(name, works, new ParallelFlowExecutor());
        }
    }
}
