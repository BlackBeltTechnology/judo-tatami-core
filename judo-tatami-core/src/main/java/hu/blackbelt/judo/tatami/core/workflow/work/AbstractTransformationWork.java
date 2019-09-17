package hu.blackbelt.judo.tatami.core.workflow.work;


public abstract class AbstractTransformationWork implements Work {

    TransformationContext transformationContext;

    public AbstractTransformationWork(TransformationContext transformationContext) {
        this.transformationContext = transformationContext;
    }

    public abstract void execute() throws Exception;

    public String getName() {
        return this.getClass().getName() + " - " + transformationContext.getModelName();
    }

    public TransformationContext getTransformationContext() {
        return transformationContext;
    }

    public WorkReport call() {
        try {
            execute();
        } catch (Exception e) {
            return new DefaultWorkReport(WorkStatus.FAILED, e);
        }
        return new DefaultWorkReport(WorkStatus.COMPLETED);
    }
}
