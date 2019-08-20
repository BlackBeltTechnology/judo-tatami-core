package hu.blackbelt.judo.tatami.core.workflow.work;

import com.google.common.collect.Maps;

import java.util.Map;

public class TransformationContext {

    String modelName;
    Map<Object, Object> variables = Maps.newConcurrentMap();

    public TransformationContext(String modelName) {
        this.modelName = modelName;
    }

    public void put(Object key, Object value) {
        variables.put(key, value);
    }

    public void put(Object value) {
        variables.put(value.getClass(), value);
    }

    public Object get(Object key) {
        return variables.get(key);
    }

    public <T> T getByClass(Class<T> key) {
        return (T) variables.get(key);
    }


    public String getModelName() {
        return this.modelName;
    }
}
