package hu.blackbelt.judo.tatami.core.workflow.work;

public interface MetricsCollector {

    void invokedTransformation(String transformationName);

    void stoppedTransformation(String transformationName, long executionTime, boolean failed);
}
