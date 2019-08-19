package hu.blackbelt.judo.tatami.core.workflow.work;


public abstract class AbstractTransformationWork implements Work {
    public abstract String getName();
    public abstract WorkReport call();
}
