# judo-tatami-core/src/test/java/hu/blackbelt/judo/tatami/core/etl

Tests of the ETL engine registry.

| File | Purpose |
|---|---|
| `EtlEngineRegistryTest.java` | Covers `EtlEngineRegistry`: engine found via `ServiceLoader` (nested `ServiceLoadedEngineImpl`), missing engine names stage + artifact, registration after a miss is seen (misses not cached), `unregister` removes only the registered instance. See change: split-etl-artifacts. |
