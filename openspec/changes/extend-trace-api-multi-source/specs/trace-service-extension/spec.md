# Specification: Trace Service Extension for Multi-Source

**Capability**: trace-service-extension
**Version**: 1.0.0

## Overview

This capability defines new parallel methods in `TransformationTraceService` to handle multi-source trace scenarios while keeping existing single-source methods unchanged for backward compatibility.

---

## ADDED Requirements

### Requirement: SVC-001
**Title**: Multi-Source Ascendant Lookup

The service shall provide methods to find ascendants when trace entries have multiple sources.

#### Scenario: Get all source ascendants by model type
```
Given a trace with entry [source1, source2] → [target1]
And target1 is the instance to lookup
When I call getAscendantsOfInstanceByModelType(modelName, sourceModelType, target1)
Then I get List<EObject> containing all sources of that type
```

#### Scenario: Get all source ascendants across multiple types
```
Given a trace with entry [sourceA:TypeA, sourceB:TypeB] → [target]
When I call getAllAscendantsOfInstance(modelName, target)
Then I get Map<TransformationTrace, List<EObject>>
And the list contains all sources from that trace
```

---

### Requirement: SVC-002
**Title**: Multi-Source Root Ascendant Lookup

The service shall traverse multi-source traces to find root elements.

#### Scenario: Get root ascendants when multiple sources exist
```
Given a pipeline:
  root1 → [level1A, level1B]
  [level1A, level1B] → [level2]  (multi-source entry)
When I call getRootAscendantsOfInstance(modelName, level2)
Then I get List<EObject> containing [root1]
```

#### Scenario: Get root ascendants with divergent paths
```
Given a pipeline:
  rootA → [level1A]
  rootB → [level1B]
  [level1A, level1B] → [level2]  (multi-source merge)
When I call getRootAscendantsOfInstance(modelName, level2)
Then I get List<EObject> containing [rootA, rootB]
```

---

### Requirement: SVC-003
**Title**: Multi-Source Descendant Lookup

The service shall provide methods to find descendants when trace entries have multiple sources.

#### Scenario: Get descendants from multi-source entry
```
Given a trace with entry [source1, source2] → [target1, target2, target3]
When I call getDescendantsOfInstanceByModelType(modelName, targetModelType, source1)
Then I get List<EObject> containing [target1, target2, target3]
```

#### Scenario: Get all descendants across multi-source chain
```
Given a pipeline:
  root → [level1A, level1B]
  [level1A, level1B] → [level2A, level2B]  (multi-source)
When I call getAllDescendantsOfInstance(modelName, root)
Then I get Map<TransformationTrace, List<EObject>> with all levels
```

---

### Requirement: SVC-004
**Title**: Trace Entry Based API

The service shall provide methods that work with TraceEntry objects directly.

#### Scenario: Add trace entry with multiple sources
```
Given a TraceEntry with sources=[s1, s2] and targets=[t1, t2]
When I call service.addTraceEntry(modelName, traceEntry)
Then the entry is registered
And lookups from any source find the targets
And lookups from any target find all sources
```

#### Scenario: Get trace entries for instance
```
Given an EObject instance in the trace
When I call service.getTraceEntriesForInstance(modelName, instance)
Then I get List<TraceEntry> where instance is in sources or targets
```

---

## Interface Additions

### Requirement: SVC-005
**Title**: TransformationTraceService Interface Extensions

The interface shall add new methods without modifying existing signatures.

```java
// NEW: Multi-source ascendant methods
List<EObject> getAscendantsOfInstanceByModelType(String modelName, Class sourceModelType, EObject targetElement);

List<EObject> getRootAscendantsOfInstance(String modelName, EObject targetElement);

Map<TransformationTrace, List<EObject>> getAllAscendantsOfInstanceMultiSource(String modelName, EObject targetElement);

// NEW: Multi-source descendant methods
List<EObject> getDescendantsOfInstanceByModelTypes(String modelName, Class targetModelType, EObject... sourceElements);

Map<TransformationTrace, List<EObject>> getAllDescendantsOfInstanceMultiSource(String modelName, EObject... sourceElements);

// NEW: TraceEntry based methods
void addTraceEntry(String modelName, TransformationTrace trace, TraceEntry entry);

List<TraceEntry> getTraceEntriesForInstance(String modelName, EObject instance);
```

#### Scenario: Verify backward compatibility
```
Given existing code calling getAscendantOfInstanceByModelType (singular)
When the new methods are added
Then existing code compiles without changes
And existing behavior is unchanged
```

---

## MODIFIED Requirements

### Requirement: SVC-006
**Title**: TransformationTrace Interface Extension

The TransformationTrace interface shall support TraceEntry retrieval.

```java
// ADDED to TransformationTrace interface
default List<TraceEntry> getTraceEntries() {
    // Default implementation converts from legacy format
    return TransformationTraceLoader.fromLegacyFormat(getTransformationTrace());
}
```

#### Scenario: Get trace entries from existing implementation
```
Given a TransformationTrace implementation using legacy format
When I call getTraceEntries()
Then I get List<TraceEntry> converted from the legacy map
```

---

## Error Handling

### Requirement: SVC-007
**Title**: Multi-Source Lookup Error Handling

The service shall handle edge cases in multi-source lookups.

#### Scenario: No sources found in multi-source lookup
```
Given a target element not in any trace
When I call getAscendantsOfInstanceByModelType(modelName, type, target)
Then I get an empty list (not null)
```

#### Scenario: Circular reference detection
```
Given a trace configuration that would create a cycle
When traversing ascendants/descendants
Then the traversal stops without infinite loop
And a warning is logged
```

---

## Cross-References

- **trace-format-support**: Provides TraceEntry class used by these methods
- **TransformationTraceServiceImpl**: Implementation of these extensions
- **Existing tests**: Must continue passing unchanged
