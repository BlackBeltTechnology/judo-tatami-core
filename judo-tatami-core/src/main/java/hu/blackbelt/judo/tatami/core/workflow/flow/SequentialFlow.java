package hu.blackbelt.judo.tatami.core.workflow.flow;

import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReportPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A sequential flow executes a set of works in sequence.
 *
 * If a work fails, next works in the pipeline will be skipped.
 *
 */
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
        for (Work work : works) {
            workReport = work.call();
            if (workReport != null && WorkReportPredicate.FAILED.equals(workReport.getStatus())) {
                LOGGER.log(Level.INFO, "Work ''{0}'' has failed, skipping subsequent works", work.getName());
                break;
            }
        }
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

        public SequentialFlow.Builder then(Work work) {
            this.works.add(work);
            return this;
        }

        public SequentialFlow build() {
            return new SequentialFlow(name, works);
        }
    }
}
