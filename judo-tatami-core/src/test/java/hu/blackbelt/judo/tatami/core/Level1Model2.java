package hu.blackbelt.judo.tatami.core;

import lombok.ToString;

@ToString
public class Level1Model2 implements TestModel {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
