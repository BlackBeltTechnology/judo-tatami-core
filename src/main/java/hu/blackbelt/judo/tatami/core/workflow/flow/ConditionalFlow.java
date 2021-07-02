package hu.blackbelt.judo.tatami.core.workflow.flow;

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
        log.info("Call work '{}' - Call work: '{}'", new String[] {getName(), toExecute.getName()});
        WorkReport jobReport = toExecute.call();

        if (predicate.apply(jobReport)) {
            jobReport = nextOnPredicateSuccess.call();
        } else {
            //log.info("Call work {} - Predicate {} unmatched, and  '{}' FAILED", new String[] {getName(), jobReport.getStatus().name()});
            if (nextOnPredicateFailure != null && !(nextOnPredicateFailure instanceof NoOpWork)) { // else is optional
                //log.info("Call work {} - Predicate {} unmatched, and  '{}' - Call work: {}", new String[] {getName(), jobReport.getStatus().name(),
                //        toExecute.getName(), nextOnPredicateFailure.getName()});
                jobReport = nextOnPredicateFailure.call();
            }
        }
        log.info("Work {} Returns: {} ", getName(), jobReport);
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
