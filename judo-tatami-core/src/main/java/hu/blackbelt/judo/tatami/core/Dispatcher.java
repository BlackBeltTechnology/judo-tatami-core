package hu.blackbelt.judo.tatami.core;

import java.util.Map;

public interface Dispatcher {
    Map<String, Object> callOperation(String target, String operationFullyQualifiedName, Map<String, Object> payload);
}