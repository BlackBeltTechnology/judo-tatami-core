package hu.blackbelt.judo.tatami.core;

import lombok.ToString;

@ToString
public class RootModel implements TestModel {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
