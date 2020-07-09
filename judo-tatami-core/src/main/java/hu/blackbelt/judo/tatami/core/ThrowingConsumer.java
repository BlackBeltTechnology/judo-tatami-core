package hu.blackbelt.judo.tatami.core;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
	void accept(T t) throws E;

	static <T> Consumer<T> executeWrapper(boolean silent, ThrowingConsumer<T, Exception> throwingConsumer) {
		if (silent) {
			return quietConsumerWrapper(throwingConsumer);
		} else {
			return throwingConsumerWrapper(throwingConsumer);
		}
	}

	static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
		return i -> {
			try {
				throwingConsumer.accept(i);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}

	static <T> Consumer<T> quietConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
		return i -> {
			try {
				throwingConsumer.accept(i);
			} catch (Exception ex) {
			}
		};
	}

}

