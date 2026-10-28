# `ZetaTraceLoader.java`

`TraceLoader` for the Zeta JSON trace format, the multi-source counterpart of
`EtlTraceLoader`. Uses Gson with pretty printing.

`loadTrace(InputStream, List<ResourceSet>)`:

- Reads the root object's `traceEntries` array; a missing array yields an empty list,
  not an error.
- Each JSON entry carries `ruleName`, optional `discriminator`, optional `primary`, and
  `source`/`sources` + `target`/`targets` element-info objects. Both singular and plural
  keys are accepted and concatenated.
- Element info must carry an `id`; a missing `id` or an id absent from the index throws
  `TraceLoadException` naming the `ruleName` and `type`.
- Entries are grouped by private `GroupKey(source, ruleName, discriminator)` — where
  `source` is the FIRST source and is compared by IDENTITY (`==` /
  `System.identityHashCode`), not `equals`. Targets of a group are merged into one
  `TraceEntry`; `primary` is true if any member entry set it.
- Consequence: only the first source of a multi-source entry survives into the
  resulting `TraceEntry`, and identity keying means resolved elements must come from
  the same `ResourceSet` instances passed in.

`saveTrace(List<TraceEntry>, OutputStream)`:

- Flattens to the cartesian product of sources × targets — one JSON entry per pair —
  so a save/load round trip is not shape-preserving.
- Writes `traceEntries`, plus `entryCount` and a `timestamp` of
  `System.currentTimeMillis()`, which makes output non-reproducible byte-for-byte.
- Element info is `{type, id, name?}` where `type` is the `EClass` name and `name` comes
  from a `name` structural feature when present.

Index and id resolution:

- `buildIdIndex` maps both the `id` attribute value and the EMF URI fragment of every
  `EObject` in every passed `ResourceSet`; the `id` attribute wins on collision.
- `getElementId` falls back through the `id` attribute → resource URI fragment (only if
  it does not start with `/`) → synthetic `obj@<identityHashCode>`, which is NOT stable
  across JVM runs — an element without an `id` cannot be reliably reloaded.
