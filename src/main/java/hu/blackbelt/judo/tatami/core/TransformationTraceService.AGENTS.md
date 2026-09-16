# `TransformationTraceService.java`

Query API over all `TransformationTrace` services registered under one model name.
Traces form a chain (psm→asm→rdbms…); this interface walks that chain in both
directions from a single `EObject`.

Registry lifecycle:

- `add(TransformationTrace)` / `remove(TransformationTrace)` — called by
  `TransformationTraceTracker` on OSGi bind/unbind, keyed by
  `TransformationTrace.getModelName()`.

Upward (target → source) navigation:

- `getParentTransformationTraceByInstance(String modelName, EObject targetElement)` —
  trace whose target `ResourceSet` contains the element's root container.
- `getAscendantOfInstanceByModelType(String modelName, Class sourceModelType, EObject targetElement)` —
  single ancestor of a given model type; `null` model type means "walk to root".
- `getRootAscendantOfInstance`, `getAllAscendantOfInstance`,
  `getTransformationTraceAscendantsByInstance` — root ancestor, trace→element map,
  and just the trace chain.

Downward (source → target) navigation:

- `getAllDescendantOfInstance`, `getDescendantOfInstanceByModelType` — transitive
  descendants, optionally filtered by target model type.

Multi-source overloads (added for Zeta M:N traces, where one target has several sources):

- `getAscendantsOfInstanceByModelType`, `getRootAscendantsOfInstance`,
  `getAllAscendantsOfInstanceMultiSource`,
  `getDescendantsOfInstancesByModelType(String, Class, EObject...)`,
  `getAllDescendantsOfInstancesMultiSource(String, EObject...)`.
- `getTraceEntriesForInstance(String, EObject)` — raw `TraceEntry` list mentioning
  the instance on either side, carrying `ruleName`/`discriminator`.

Contracts a caller can violate:

- Every method is scoped by `modelName`; the implementation throws
  `IllegalArgumentException` when no trace was registered under that name.
- The singular `getAscendantOfInstanceByModelType` collapses a multi-source mapping
  to one element — use the `…Ascendants…` plural form for Zeta traces.
- Elements are matched by resource URI + fragment, not by identity, so an element
  detached from its `Resource` will not be found.
