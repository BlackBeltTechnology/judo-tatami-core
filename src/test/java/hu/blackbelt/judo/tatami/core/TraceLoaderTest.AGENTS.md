# `TraceLoaderTest.java`

Unit tests for trace persistence: format detection, entry construction, save/load
round-trips, and format conversion.

## Fixture

`@TempDir File tempDir` receives every written trace file. `setUp()` builds two
`ResourceSet`s via `createResourceSetWithObjects` — a `source` model holding
`src1`/`src2` and a `target` model holding `tgt1`/`tgt2`/`tgt3` — using
`EcoreBuilders` plus `EcoreResourceFactoryImpl`. Loaders under test resolve
references against these two sets passed as a list.

## Covered behaviour

- `testTraceFormatDetection` — `TraceFormat.detect(fileName)` maps `.json` to
  `TraceFormat.ZETA_JSON` and the XMI-style names to the ETL format.
- `testTraceEntryBuilder`, `testTraceEntryMultipleSources` — `TraceEntry.builder()`
  accepts a single source/target as well as multiple sources; `getSources()`
  reports all of them.
- `testZetaTraceLoaderSaveAndLoad`, `testZetaTraceLoaderMultiSource` — `ZetaTraceLoader`
  writes then re-reads JSON traces, including entries with several sources.
- `testEtlTraceLoaderSaveAndLoad` — `EtlTraceLoader` round-trips the XMI trace form.
- `testTransformationTraceLoaderUnifiedApi` — the unified `TransformationTraceLoader`
  dispatches to the right concrete loader by detected format.
- `testLegacyFormatConversion`, `testMultiSourceFormatConversion` — legacy
  single-source payloads and multi-source payloads convert into the current
  `TraceEntry` shape.

## Contracts asserted

- `ZetaTraceLoader.loadTrace` throws `TraceLoadException` when a JSON entry names
  an id absent from the supplied resource sets (`"nonexistent_id"`).
- `EtlTraceLoader.loadTrace`, constructed as `new EtlTraceLoader("test")`, throws
  `TraceLoadException` on an unresolvable `sourceUri`.
- `TraceEntry.builder().build()` with nothing set yields `null` from `getSource()`
  and `getTarget()`, and empty (not null) `getSources()` / `getTargets()`.

A loader that silently drops an unresolvable reference instead of throwing breaks
these tests by design — unresolved references must fail loudly.
