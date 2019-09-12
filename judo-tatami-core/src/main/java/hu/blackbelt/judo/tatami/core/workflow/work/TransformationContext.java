package hu.blackbelt.judo.tatami.core.workflow.work;

import com.google.common.collect.Maps;

import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransformationContext {

    String modelName;
    Map<Object, Object> variables = Maps.newConcurrentMap();
    public TransformationContextVerifier transformationContextVerifier;

    public TransformationContext(String modelName) {
        this.modelName = modelName;
        transformationContextVerifier = new TransformationContextVerifier(this);
    }

    public void put(Object key, Object value) {
        variables.put(key, value);
    }

    public void put(Object value) {
        variables.put(value.getClass(), value);
    }

    public Optional<Object> get(Object key) {
        return ofNullable(variables.get(key));
    }

    public <T> Optional<T> get(Class<T> valueType, Object key) {
        return ofNullable((T) variables.get(key));
    }

    
    public <T> Optional<T> getByClass(Class<T> key) {
    	ofNullable(key).orElseThrow(() -> new IllegalAccessError("Key is null"));
    	return ofNullable((T) variables.get(key));
    }


    public String getModelName() {
        return this.modelName;
    }
    
    public class TransformationContextVerifier {
		boolean allClassExists = true;
		TransformationContext transformationContext;

		public TransformationContextVerifier(TransformationContext transformationContext) {
			this.transformationContext = transformationContext;
		}

		private boolean verifyClassPresent(Class c) {
			if (!transformationContext.getByClass(c).isPresent()) {
				log.error("Missing from transformation context: " + String.valueOf(c).replace("class", ""));
				return false;
			}
			return true;
		}

		public TransformationContextVerifier isClassExists(Class c) {
			allClassExists = allClassExists && verifyClassPresent(c);
			return this;
		}

		public boolean isAllExists() {
			return allClassExists;
		}
	}
}
