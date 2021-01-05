package hu.blackbelt.judo.tatami.core.workflow.work;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTransformationWork implements Work {

    TransformationContext transformationContext;
    protected MetricsCollector metricsCollector;

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

    public AbstractTransformationWork withMetricsCollector(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
        return this;
    }

    public WorkReport call() {
        if (metricsCollector != null) {
            metricsCollector.invokedTransformation(getClass().getName());
        }
        final Long startTs = System.nanoTime();
        boolean failed = false;
        try {
            execute();
            return new DefaultWorkReport(WorkStatus.COMPLETED);
        } catch (Exception e) {
            failed = true;
            return new DefaultWorkReport(WorkStatus.FAILED, e);
        } finally {
            if (metricsCollector != null) {
                metricsCollector.stoppedTransformation(getClass().getName(), System.nanoTime() - startTs, failed);
            }
        }
    }
}
