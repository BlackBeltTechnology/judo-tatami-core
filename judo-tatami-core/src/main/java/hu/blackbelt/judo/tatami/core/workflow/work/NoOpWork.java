package hu.blackbelt.judo.tatami.core.workflow.work;

import java.util.UUID;

/**
 * No operation work.
 */
public class NoOpWork implements Work {

    @Override
    public String getName() {
        return UUID.randomUUID().toString();
    }

    @Override
    public WorkReport call() {
        return new DefaultWorkReport(WorkStatus.COMPLETED);
    }
}
