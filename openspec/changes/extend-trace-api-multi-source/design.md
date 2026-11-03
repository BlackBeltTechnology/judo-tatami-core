# Design: Extend Trace API for Multi-Source Support

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                    TransformationTraceService                        │
│  (unchanged API - backward compatible)                              │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      TransformationTrace                             │
│  MODIFIED: getTransformationTrace() returns MultiSourceTraceMap     │
│  NEW: getMultiSourceTransformationTrace()                           │
│  NEW: getLegacyTransformationTrace() (adapter)                      │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
┌──────────────────────────┐    ┌──────────────────────────┐
│      TraceEntry          │    │     TraceFormat          │
│  NEW: Multi-source model │    │  ETL_XMI | ZETA_JSON     │
└──────────────────────────┘    └──────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
┌───────────────────┐   ┌───────────────────┐
│  EtlTraceLoader   │   │  ZetaTraceLoader  │
│  (XMI format)     │   │  (JSON format)    │
└───────────────────┘   └───────────────────┘
```

## Data Model

### Current Model (ETL)

```
Map<EObject, List<EObject>>
     │              │
     └─ source      └─ targets (1:N)
```

### New Model (Multi-Source)

```
TraceEntry
├── sources: List<EObject>     (M sources)
├── targets: List<EObject>     (N targets)
├── ruleName: String           (optional)
├── discriminator: String      (optional)
└── primary: boolean           (optional)

MultiSourceTraceMap = List<TraceEntry>
```

### Mapping Between Formats

```
ETL Format (1:N)                    Zeta Format (1:1 per entry)
────────────────                    ──────────────────────────
source1 → [target1, target2]        entry1: source1 → target1, rule=R1
                                    entry2: source1 → target2, rule=R1

Multi-Source Format (M:N)
─────────────────────────
[source1, source2] → [target1, target2, target3]
```

## Component Design

### 1. TraceEntry Class

```java
@Builder
@Getter
public class TraceEntry {
    private final List<EObject> sources;
    private final List<EObject> targets;
    private final String ruleName;
    private final String discriminator;
    private final boolean primary;

    // Convenience for single source
    public EObject getSource() {
        return sources.isEmpty() ? null : sources.get(0);
    }

    // Convenience for single target
    public EObject getTarget() {
        return targets.isEmpty() ? null : targets.get(0);
    }
}
```

### 2. TraceFormat Enum

```java
public enum TraceFormat {
    ETL_XMI,     // Legacy Epsilon ETL XMI format
    ZETA_JSON;   // Zeta JSON format

    public static TraceFormat detect(String filename) {
        if (filename.endsWith(".json")) return ZETA_JSON;
        return ETL_XMI;
    }
}
```

### 3. TraceLoader Interface

```java
public interface TraceLoader {

    List<TraceEntry> loadTrace(
        InputStream input,
        List<ResourceSet> resourcesToResolve
    ) throws TraceLoadException;

    void saveTrace(
        List<TraceEntry> entries,
        OutputStream output
    ) throws TraceSaveException;
}
```

### 4. EtlTraceLoader (Simplified)

```java
@Slf4j
public class EtlTraceLoader implements TraceLoader {

    @Override
    public List<TraceEntry> loadTrace(InputStream input, List<ResourceSet> resources) {
        // Direct XMI parsing without dynamic metamodel
        // Parse sourceUri and targetUri attributes directly
        // Resolve URIs against provided ResourceSets
    }

    @Override
    public void saveTrace(List<TraceEntry> entries, OutputStream output) {
        // Write XMI directly with trace namespace
        // No EPackage registration needed
    }
}
```

### 5. ZetaTraceLoader

```java
@Slf4j
public class ZetaTraceLoader implements TraceLoader {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public List<TraceEntry> loadTrace(InputStream input, List<ResourceSet> resources) {
        // Parse JSON using Gson
        // Resolve element IDs against provided ResourceSets
    }

    @Override
    public void saveTrace(List<TraceEntry> entries, OutputStream output) {
        // Serialize to JSON using Gson
    }
}
```

### 6. TransformationTraceLoader (Updated)

```java
@Slf4j
public class TransformationTraceLoader {

    // NEW: Unified loading with format detection
    public static List<TraceEntry> loadTrace(
            InputStream input,
            TraceFormat format,
            List<ResourceSet> resourcesToResolve) {
        TraceLoader loader = format == TraceFormat.ZETA_JSON
            ? new ZetaTraceLoader()
            : new EtlTraceLoader();
        return loader.loadTrace(input, resourcesToResolve);
    }

    // LEGACY: Backward compatible method (deprecated)
    @Deprecated
    public static Map<EObject, List<EObject>> resolveTransformationTraceAsEObjectMap(
            List<EObject> traceEntries,
            List<ResourceSet> resourcesToResolve) {
        // Keep existing implementation for backward compatibility
    }

    // NEW: Convert TraceEntry list to legacy format
    public static Map<EObject, List<EObject>> toLegacyFormat(List<TraceEntry> entries) {
        Map<EObject, List<EObject>> result = new HashMap<>();
        for (TraceEntry entry : entries) {
            EObject source = entry.getSource();
            if (source != null) {
                result.computeIfAbsent(source, k -> new ArrayList<>())
                      .addAll(entry.getTargets());
            }
        }
        return new EMapWrapper<>(ECollections.asEMap(result));
    }

    // NEW: Convert TraceEntry list to multi-source format
    public static Map<List<EObject>, List<EObject>> toMultiSourceFormat(List<TraceEntry> entries) {
        // Group by sources list
    }
}
```

## JSON Schema (Zeta Format)

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "traceEntries": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "ruleName": { "type": "string" },
          "source": { "$ref": "#/definitions/elementInfo" },
          "sources": {
            "type": "array",
            "items": { "$ref": "#/definitions/elementInfo" }
          },
          "target": { "$ref": "#/definitions/elementInfo" },
          "targets": {
            "type": "array",
            "items": { "$ref": "#/definitions/elementInfo" }
          },
          "discriminator": { "type": "string" },
          "primary": { "type": "boolean" }
        }
      }
    },
    "entryCount": { "type": "integer" },
    "timestamp": { "type": "integer" }
  },
  "definitions": {
    "elementInfo": {
      "type": "object",
      "properties": {
        "type": { "type": "string" },
        "id": { "type": "string" },
        "name": { "type": "string" }
      },
      "required": ["type", "id"]
    }
  }
}
```

## Migration Strategy

### Phase 1: Add New Components
- Add `TraceEntry`, `TraceFormat`, `TraceLoader` interface
- Add `ZetaTraceLoader` implementation
- Add `EtlTraceLoader` (simplified, no dynamic metamodel)

### Phase 2: Update TransformationTraceLoader
- Add new unified `loadTrace()` method
- Add format conversion utilities
- Deprecate old methods (keep for backward compatibility)

### Phase 3: Update TransformationTrace Interface
- Add `getMultiSourceTransformationTrace()` method
- Keep `getTransformationTrace()` as legacy adapter

### Phase 4: Comprehensive Tests
- Unit tests for each loader
- Integration tests for format conversion
- Multi-level trace traversal tests

## Backward Compatibility

| Existing Method | Status | Migration Path |
|-----------------|--------|----------------|
| `resolveTransformationTraceAsEObjectMap()` | Deprecated | Use `loadTrace()` + `toLegacyFormat()` |
| `createTraceResourceSet()` | Deprecated | No longer needed for direct load/save |
| `getTransformationTraceFromTraceMap()` | Deprecated | Use `saveTrace()` |
| `TransformationTrace.getTransformationTrace()` | Keep | Returns legacy format via adapter |

## Design Decisions

### D1: Element ID Resolution Strategy
**Decision**: Match JSON `id` field against EObject's `id` EAttribute (not URI fragment)

**Rationale**:
- Zeta traces use semantic IDs from the model (e.g., `entity-customer`)
- URI fragments are implementation-dependent and may change
- The `id` attribute is stable and meaningful

**Implementation**:
```java
private EObject resolveById(String id, List<ResourceSet> resourceSets) {
    for (ResourceSet rs : resourceSets) {
        for (Resource r : rs.getResources()) {
            for (EObject obj : getAllContents(r)) {
                EStructuralFeature idFeature = obj.eClass().getEStructuralFeature("id");
                if (idFeature != null && id.equals(obj.eGet(idFeature))) {
                    return obj;
                }
            }
        }
    }
    throw new TraceLoadException("Element not found: " + id);
}
```

### D2: Entry Grouping Strategy
**Decision**: Group Zeta JSON entries by source + ruleName + discriminator (all must match)

**Rationale**:
- Preserves rule metadata integrity
- Entries from different rules remain distinguishable
- Supports discriminated equivalence patterns

**Implementation**:
```java
// Group key includes source identity + metadata
record GroupKey(EObject source, String ruleName, String discriminator) {}

Map<GroupKey, List<EObject>> grouped = entries.stream()
    .collect(Collectors.groupingBy(
        e -> new GroupKey(e.getSource(), e.getRuleName(), e.getDiscriminator()),
        Collectors.flatMapping(e -> e.getTargets().stream(), Collectors.toList())
    ));

return grouped.entrySet().stream()
    .map(e -> TraceEntry.builder()
        .sources(List.of(e.getKey().source()))
        .targets(e.getValue())
        .ruleName(e.getKey().ruleName())
        .discriminator(e.getKey().discriminator())
        .build())
    .toList();
```

### D3: Error Handling Strategy
**Decision**: Fail fast with TraceLoadException for unresolved references

**Rationale**:
- Trace integrity is critical for debugging transformations
- Silent failures hide configuration errors
- Clear error messages speed up troubleshooting

**Implementation**:
```java
if (resolved == null) {
    throw new TraceLoadException(
        "Element not found: id=" + id +
        ", ruleName=" + entry.getRuleName() +
        ", type=" + elementInfo.getType()
    );
}
```

### D4: ETL XMI Resolution Strategy
**Decision**: Try URI fragment first, fall back to ID attribute

**Rationale**:
- Backward compatibility with existing ETL traces using URI fragments
- Support for newer models that may use ID attributes
- Graceful handling of mixed environments

**Implementation**:
```java
private EObject resolveEtlUri(String uriString, List<ResourceSet> resourceSets) {
    URI uri = URI.createURI(uriString);
    String fragment = uri.fragment();

    // Strategy 1: Try URI fragment match (original behavior)
    for (ResourceSet rs : resourceSets) {
        for (Resource r : rs.getResources()) {
            EObject obj = r.getEObject(fragment);
            if (obj != null) {
                return obj;
            }
        }
    }

    // Strategy 2: Fall back to ID attribute match
    for (ResourceSet rs : resourceSets) {
        for (EObject obj : getAllContents(rs)) {
            EStructuralFeature idFeature = obj.eClass().getEStructuralFeature("id");
            if (idFeature != null && fragment.equals(obj.eGet(idFeature))) {
                return obj;
            }
        }
    }

    throw new TraceLoadException("Element not found: " + uriString);
}
```

---

## Performance Considerations

1. **Indexed Resolution**: Build ID→EObject index once per load operation
2. **Caching**: Cache resolved elements by ID for repeated lookups
3. **Streaming**: Support streaming for large trace files
4. **Direct Serialization**: Avoid EMF metamodel overhead
