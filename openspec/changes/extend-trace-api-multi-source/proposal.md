# Proposal: Extend Trace API for Multi-Source Support and Zeta JSON Format

**Change ID**: `extend-trace-api-multi-source`
**Status**: Draft
**Created**: 2024-12-14

## Summary

Extend the transformation trace API to support:
1. **Multiple sources → multiple targets** mapping (Zeta semantics)
2. **Direct JSON-based trace loading/saving** without dynamic EMF metamodel creation
3. **Both ETL (XMI) and Zeta (JSON) trace formats** for loading
4. **Backward compatibility** for existing external callers

## Problem Statement

### Current Limitations

1. **Single Source Constraint**: Current `TransformationTraceLoader` supports only `single source → multiple targets` mapping:
   ```java
   Map<EObject, List<EObject>> // Current: one source to many targets
   ```

2. **Dynamic Metamodel Overhead**: The current implementation creates a pseudo EMF metamodel on-the-fly for XMI serialization, which is:
   - Complex to maintain
   - Requires EMF package registration
   - Not suitable for JSON-based traces

3. **No Zeta JSON Support**: The Zeta transformation framework uses a JSON trace format with:
   - `ruleName` - transformation rule identifier
   - `source` - single source element info
   - `target` - single target element info
   - `discriminator` - optional discriminator for multi-target scenarios
   - `primary` - flag for primary transformations

### Zeta Trace Format (JSON)

```json
{
  "traceEntries": [
    {
      "ruleName": "EntityType2Table",
      "source": { "type": "EntityType", "id": "entity-1", "name": "Customer" },
      "target": { "type": "Table", "id": "table-1", "name": "Customer" },
      "primary": true
    }
  ],
  "entryCount": 1,
  "timestamp": 1699123456789
}
```

### ETL Trace Format (XMI)

```xml
<trace:Trace sourceUri="psm:model#_abc123">
  <targetUri>asm:model#_def456</targetUri>
  <targetUri>asm:model#_ghi789</targetUri>
</trace:Trace>
```

## Proposed Solution

### 1. New Multi-Source Trace Model

Introduce a new trace entry structure supporting multiple sources:

```java
public class TraceEntry {
    List<EObject> sources;     // Multiple sources (Zeta support)
    List<EObject> targets;     // Multiple targets
    String ruleName;           // Optional rule name
    String discriminator;      // Optional discriminator
    boolean primary;           // Primary mapping flag
}
```

### 2. Direct Load/Save Without Dynamic Metamodel

Replace dynamic EMF metamodel creation with direct serialization:
- **JSON format**: Use Gson for Zeta trace files
- **XMI format**: Use direct EObject URI mapping (no pseudo package)

### 3. Unified Trace API

```java
public interface TraceLoader {
    Map<List<EObject>, List<EObject>> loadTrace(InputStream input, TraceFormat format);
    void saveTrace(Map<List<EObject>, List<EObject>> trace, OutputStream output, TraceFormat format);
}

public enum TraceFormat {
    ETL_XMI,    // Legacy ETL format
    ZETA_JSON   // New Zeta format
}
```

### 4. Backward Compatibility Layer

Provide adapter methods for existing consumers:

```java
// Convert new multi-source format to legacy single-source format
Map<EObject, List<EObject>> toLegacyFormat(Map<List<EObject>, List<EObject>> multiSourceTrace);
```

### 5. TransformationTraceService Extension

Add **parallel methods** (not modify existing) for multi-source scenarios:

```java
// NEW: Multi-source ascendant methods (existing methods unchanged)
List<EObject> getAscendantsOfInstanceByModelType(String modelName, Class type, EObject target);
List<EObject> getRootAscendantsOfInstance(String modelName, EObject target);

// NEW: Multi-source descendant methods
List<EObject> getDescendantsOfInstanceByModelTypes(String modelName, Class type, EObject... sources);

// NEW: TraceEntry based API
void addTraceEntry(String modelName, TransformationTrace trace, TraceEntry entry);
List<TraceEntry> getTraceEntriesForInstance(String modelName, EObject instance);
```

## Benefits

1. **Zeta Integration**: Native support for Zeta JSON traces
2. **Simplified Implementation**: No dynamic metamodel creation
3. **Multiple Sources**: Support for Zeta's multi-source semantics
4. **Dual Format**: Load both ETL and Zeta traces
5. **Backward Compatible**: Existing callers continue to work unchanged
6. **Parallel API**: New methods for multi-source without breaking existing code

## Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| Breaking existing consumers | Provide adapter layer for legacy format |
| Performance regression | Direct load/save should be faster than EMF |
| Format detection errors | Use file extension or explicit format parameter |

## Success Criteria

1. Load ETL XMI traces successfully
2. Load Zeta JSON traces successfully
3. Save traces in both formats
4. Support multi-source → multi-target mappings
5. Existing tests continue to pass
6. New comprehensive test coverage for both formats
