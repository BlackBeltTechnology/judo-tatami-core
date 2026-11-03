# `TransformationMode.java`

Enum selecting the transformation engine: `ETL` (Epsilon scripts), `ZETA` (Java), `DUAL` (runs both, compares output).

## Exports

- `SYSTEM_PROPERTY = "judo.transformation.mode"`, `DEFAULT = ZETA`.
- `CONFIG_PID = "hu.blackbelt.judo.tatami.transformation"`, `CONFIG_KEY = "mode"` — OSGi configuration coordinates.
- `static setConfiguredDefault(TransformationMode)` / `getConfiguredDefault()` — volatile container-wide default; `null` clears.
- `static parse(String)` — `Optional<TransformationMode>`; trimmed, case-insensitive; empty for null/blank/unknown.
- `fromSystemProperty()`, `isZeta()`, `isEtl()`, `isDual()`.

## Precedence of `fromSystemProperty()`

1. System property `judo.transformation.mode`, when a valid value.
2. PID configured default (set by `TransformationModeConfiguration`).
3. `DEFAULT` (ZETA).

Invalid property value is ignored silently (falls through to 2/3). Name is historical: it now also reads the PID default.

## Contracts

- Property beats PID: `-Djudo.transformation.mode=ZETA` overrides `mode = ETL` in cfg.
- Configured default is JVM-static, not per-bundle/per-workflow.
- ETL/DUAL still need the matching `-etl` add-ons; else `EtlEngineMissingException`.
- Tests: `TransformationModeTest` (src/test).

See change: split-etl-artifacts.
