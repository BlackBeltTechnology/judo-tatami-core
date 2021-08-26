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
		TransformationContext transformationContext;

		public TransformationContextVerifier(TransformationContext transformationContext) {
			this.transformationContext = transformationContext;
		}

		public  <T> boolean verifyClassPresent(Class<T> c) {
			return transformationContext.getByClass(c).isPresent();
		}

		public <T> boolean verifyKeyPresent(Object key) {
			return transformationContext.get(key).isPresent();
		}

		public <T> boolean verifyKeyPresent(Class<T> valueType, Object key) {
			return transformationContext.get(valueType, key).isPresent();
		}
	}
}
