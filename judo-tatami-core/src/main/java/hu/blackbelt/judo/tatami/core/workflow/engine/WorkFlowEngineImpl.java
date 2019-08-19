package hu.blackbelt.judo.tatami.core.workflow.engine;

import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class WorkFlowEngineImpl implements WorkFlowEngine {

    public WorkReport run(WorkFlow workFlow) {
        log.info("Running workflow ''{0}''", workFlow.getName());
        return workFlow.call();
    }

}
