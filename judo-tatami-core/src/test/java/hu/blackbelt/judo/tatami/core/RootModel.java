package hu.blackbelt.judo.tatami.core;

public class RootModel implements TestModel {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
