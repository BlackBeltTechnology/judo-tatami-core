# `TransformationTraceLoader.java`

Static façade over the `TraceLoader` SPI plus the legacy EMF pseudo-metamodel that
predates it. Two layers live in one file.

Current layer — format-dispatching load/save:

- `loadTrace(File, List<ResourceSet>)`, `loadTrace(File, TraceFormat, List<ResourceSet>)`,
  `loadTrace(InputStream, TraceFormat, List<ResourceSet>)`.
- `saveTrace(List<TraceEntry>, File)`, `saveTrace(List<TraceEntry>, File, TraceFormat)`,
  `saveTrace(List<TraceEntry>, OutputStream, TraceFormat)`.
- Private `createLoader(TraceFormat)` returns `ZetaTraceLoader` for `ZETA_JSON`,
  otherwise `EtlTraceLoader`. The `File` overloads derive the format from the file
  name via `TraceFormat.detect`, so renaming `.json`→`.xmi` silently switches engines.

Format conversion helpers (used by `TransformationTrace` default methods):

- `toLegacyFormat(List<TraceEntry>)` → `Map<EObject,List<EObject>>` keyed by the FIRST
  source only, wrapped in `EMapWrapper`; multi-source entries lose their extra sources
  and entries with a null source are dropped.
- `toMultiSourceFormat(List<TraceEntry>)` → `Map<List<EObject>,List<EObject>>`, keyed by
  a fresh `ArrayList` of sources — two entries with equal source lists collide.
- `fromLegacyFormat(Map<EObject,List<EObject>>)` → `TraceEntry` list with null
  `ruleName`/`discriminator` and `primary == false`.

Deprecated legacy layer (retained for old callers; all `@Deprecated`):

- `resolveTransformationTraceAsEObjectMap(List<EObject>, List<ResourceSet>)` — resolves
  `sourceUri`/`targetUri` attributes against a fragment index, throwing plain
  `RuntimeException` (not `TraceLoadException`) on an unresolved entry.
- `getTransformationTraceFromTraceMap(String nameSpace, Map<EObject,List<EObject>>)` —
  builds `Trace` EObjects; targets with no `eResource()` are logged and skipped, not failed.
- `createTraceResourceSet(String[, URIHandler])`, `createTraceModelResource`,
  `createTraceModelResourceFromTraceList`, `createTraceModelResourceFromEObjectMap` —
  build an on-the-fly `EPackage` with nsURI
  `http:///www.blackbelt.hu/meta/trasformation/trace/<nameSpace>` holding one `Trace`
  EClass with `sourceUri : EString` and unbounded `targetUri : EString`, registered on a
  fresh `ResourceSetImpl` under resource URI `trace:<nameSpace>`.

Contract: the namespace string is part of the on-disk format — reading a legacy trace
written under a different `nameSpace` will not resolve its `EClass`.
