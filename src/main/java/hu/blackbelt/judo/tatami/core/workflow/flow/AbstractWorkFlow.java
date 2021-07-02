package hu.blackbelt.judo.tatami.core.workflow.flow;

abstract class AbstractWorkFlow implements WorkFlow {

    private String name;

    AbstractWorkFlow(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
