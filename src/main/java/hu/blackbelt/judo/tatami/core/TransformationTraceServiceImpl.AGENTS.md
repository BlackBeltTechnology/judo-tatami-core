# `TransformationTraceServiceImpl.java`

OSGi `@Component(service = TransformationTraceService.class)` implementing the whole
`TransformationTraceService` query API over an in-memory
`Map<String modelName, List<TransformationTrace>>` (`modelNameCache`, a concurrent map).

Lifecycle:

- `@Activate activate(BundleContext)` → `openTracker` constructs a
  `TransformationTraceTracker` and opens it with `open(true)`.
- `@Deactivate deactivate()` → `closeTracker()`.
- `add`/`remove` mutate `modelNameCache`; the key is dropped once its list empties.

Traversal mechanics:

- Upward: `getParentTransformationTraceByInstance` takes
  `EcoreUtil.getRootContainer(element)` and scans every registered trace's target
  `ResourceSet` for a root equal to it. Ancestor walks loop on that until no parent
  trace is found.
- Downward: package-visible `getChildTransformationTracesByInstance` does the mirror
  scan over each trace's source `ResourceSet`s; descendant walks BFS over
  `getTransformationTrace().get(source)`.
- Multi-source walks (`getAscendantsOfInstanceByModelType`,
  `getRootAscendantsOfInstance`, `getAllAscendantsOfInstanceMultiSource`,
  `getAllDescendantsOfInstancesMultiSource`) go through
  `getTraceSourceElementsByTargetElement`, which reads `TraceEntry` objects rather
  than the legacy `Map<EObject,List<EObject>>`, so all sources of an M:N mapping survive.

`public static boolean equals(EObject, EObject)` is the element identity rule used
everywhere here: when both objects have a resource with the same URI it compares
`getURIFragment`, otherwise it falls back to `EcoreUtil.equals` (structural).

Contracts a caller can violate:

- Every public query throws `IllegalArgumentException("No model defined: …")` when
  `modelName` was never registered — register the trace before querying.
- `getAllAscendantOfInstance` maps trace → source element and returns `null` (not an
  empty map) if the walk ever leaves the loop by exhausting `current`.
- Empty trace buckets are pruned from the returned maps, so a trace in the chain that
  contributed no element is absent rather than mapped to an empty list.
- The downward walk has no visited set; a cyclic trace graph would not terminate.
