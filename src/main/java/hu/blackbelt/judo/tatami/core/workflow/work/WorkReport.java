package hu.blackbelt.judo.tatami.core.workflow.work;

/**
 * Report of work execution.
 */
public interface WorkReport {

    /**
     * Get work execution status.
     * @return execution status
     */
    WorkStatus getStatus();

    /**
     * Get error if any.
     *
     * @return error
     */
    Throwable getError();

}
