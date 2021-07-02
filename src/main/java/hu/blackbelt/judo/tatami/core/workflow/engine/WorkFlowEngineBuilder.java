package hu.blackbelt.judo.tatami.core.workflow.engine;

/**
 * Main entry point to create {@link WorkFlowEngine} instances.
 *
 */
public class WorkFlowEngineBuilder {

    /**
     * Create a new {@link WorkFlowEngineBuilder}.
     * @return a new {@link WorkFlowEngineBuilder}.
     */
    public static WorkFlowEngineBuilder aNewWorkFlowEngine() {
        return new WorkFlowEngineBuilder();
    }

    private WorkFlowEngineBuilder() {
    }

    /**
     * Create a new {@link WorkFlowEngine}.
     * @return a new {@link WorkFlowEngine}.
     */
    public WorkFlowEngine build() {
        return new WorkFlowEngineImpl();
    }
}
