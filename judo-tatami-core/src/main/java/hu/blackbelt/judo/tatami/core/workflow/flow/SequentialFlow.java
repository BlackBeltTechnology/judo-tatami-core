package hu.blackbelt.judo.tatami.core.workflow.flow;

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

        log.info("Call work '{}' - Call work:  '{}' ", new String[] {getName(),
                works.stream().map(w -> w.getName()).collect(Collectors.joining(", "))});

        if (works.size() == 0) {
            return new DefaultWorkReport(WorkStatus.COMPLETED);
        }
        for (Work work : works) {
            workReport = work.call();
            if (workReport != null && WorkReportPredicate.FAILED.apply(workReport)) {
                log.error(String.format("Work '%s' has failed, skipping subsequent works", work.getName()));
                break;
            }
        }
        log.info("Work {} Returns: {} ", getName(), workReport);
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
