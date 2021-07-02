package hu.blackbelt.judo.tatami.core.workflow.engine;

import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;

/**
 * Interface for workflow engine.
 *
 */
public interface WorkFlowEngine {

    /**
     * Run the given workflow and return its report.
     *
     * @param workFlow to run
     * @return workflow report
     */
    WorkReport run(WorkFlow workFlow);

}
