package hu.blackbelt.judo.tatami.core.workflow.work;

/**
 * Default implementation of {@link WorkReport}.
 */
public class DefaultWorkReport implements WorkReport {

    private WorkStatus status;
    private Throwable error;

    /**
     * Create a new {@link DefaultWorkReport}.
     *
     * @param status of work
     */
    public DefaultWorkReport(WorkStatus status) {
        this.status = status;
    }

    /**
     * Create a new {@link DefaultWorkReport}.
     *
     * @param status of work
     * @param error if any
     */
    public DefaultWorkReport(WorkStatus status, Throwable error) {
        this(status);
        this.error = error;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "DefaultWorkReport {" +
                "status=" + status +
                ", error=" + (error == null ? "''" : error) +
                '}';
    }
}
