package hu.blackbelt.judo.tatami.core.workflow.work;

import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkArgument;
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

    @SuppressWarnings("unchecked")
	public <T> Optional<T> get(Class<T> valueType, Object key) {
        Optional<T> ret = (Optional<T>) ofNullable(variables.get(key));
        checkArgument(!ret.filter(o -> !valueType.isAssignableFrom(o.getClass())).isPresent(), "Required type and value is not match");
        return ret;
    }

    
    @SuppressWarnings("unchecked")
	public <T> Optional<T> getByClass(Class<T> key) {
    	ofNullable(key).orElseThrow(() -> new IllegalAccessError("Key is null"));
    	return ofNullable((T) variables.get(key));
    }


    public String getModelName() {
        return this.modelName;
    }
    
    public class TransformationContextVerifier {
		boolean allExists = true;
		TransformationContext transformationContext;

		public TransformationContextVerifier(TransformationContext transformationContext) {
			this.transformationContext = transformationContext;
		}

		private <T> boolean verifyClassPresent(Class<T> c) {
			if (!transformationContext.getByClass(c).isPresent()) {
				log.error("Missing from transformation context: " + c.getName());
				return false;
			}
			return true;
		}
		
		private <T> boolean verifyKeyPresent(Object key) {
			if (!transformationContext.get(key).isPresent()) {
				log.error("Missing from transformation context: " + String.valueOf(key));
				return false;
			}
			return true;
		}
		
		private <T> boolean verifyKeyPresent(Class<T> valueType, Object key) {
			if (!transformationContext.get(valueType, key).isPresent()) {
				log.error("Missing from transformation context: " + valueType.getName()+ " " + String.valueOf(key));
				return false;
			}
			return true;
		}

		public <T> TransformationContextVerifier isClassExists(Class<T> c) {
			allExists = allExists && verifyClassPresent(c);
			return this;
		}
		
		public <T> TransformationContextVerifier isKeyExists(Object key) {
			allExists = allExists && verifyKeyPresent(key);
			return this;
		}
		
		public <T> TransformationContextVerifier isKeyExists(Class<T> valueType, Object key)  {
			allExists = allExists && verifyKeyPresent(valueType, key);
			return this;
		}
		
		public <T> TransformationContextVerifier isMultipleKeyExists(Class<T> valueType, Object[] keys) {
			for (Object key : keys) {
				allExists = allExists && verifyKeyPresent(valueType, key);
			}
			return this;
		}

		public boolean isAllExists() {
			return allExists;
		}
	}
}
