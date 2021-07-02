package hu.blackbelt.judo.tatami.core;

// We need to describe supplier which can throw exceptions
@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;

    // Now, wrapper
    static  <T> T sneakyThrows(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

