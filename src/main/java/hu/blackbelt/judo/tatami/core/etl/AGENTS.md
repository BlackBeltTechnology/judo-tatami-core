# judo-tatami-core/src/main/java/hu/blackbelt/judo/tatami/core/etl

Engine-lookup extension point for the optional per-stage ETL add-ons (`judo-tatami-X-etl`). Keeps core Epsilon-free: a `ZETA` run never reaches this package.

| File | Purpose |
|---|---|
| `EtlEngineMissingException.java` | `IllegalStateException` thrown when ETL/DUAL mode finds no engine. Message names the stage interface and add-on Maven coordinates; exports `getEngineType()`, `getArtifactCoordinates()`. See change: split-etl-artifacts. |
| `EtlEngineRegistry.java` | Final static registry. `register`/`unregister(Class<E>, E)` (OSGi DS components, tests), `find(Class<E>)` → `Optional` (registered engine first, then `ServiceLoader` on context then own class loader), `require(Class<E>, coordinates)` throws `EtlEngineMissingException`. Misses never cached (OSGi start order). See change: split-etl-artifacts. |
