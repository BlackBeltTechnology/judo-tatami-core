package hu.blackbelt.judo.tatami.core;

import org.eclipse.emf.ecore.EOperation;

import java.util.Map;

public interface Dispatcher {
    Map<String, Object> callOperation(EOperation operation, Map<String, Object> payload);
}
