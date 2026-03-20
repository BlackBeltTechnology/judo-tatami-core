# Specification: Trace Format Support

**Capability**: trace-format-support
**Version**: 1.0.0

## Overview

This capability defines requirements for loading and saving transformation traces in multiple formats (ETL XMI and Zeta JSON) with support for multi-source mappings.

---

## ADDED Requirements

### Requirement: TRACE-001
**Title**: TraceEntry Data Model

The system shall provide a `TraceEntry` class that represents a single trace mapping with support for multiple sources and targets.

#### Scenario: Create trace entry with single source and target
```
Given a source EObject "entityA"
And a target EObject "tableA"
When I create a TraceEntry with source=[entityA] and targets=[tableA]
Then the TraceEntry.getSource() returns "entityA"
And the TraceEntry.getTarget() returns "tableA"
And the TraceEntry.getSources() returns [entityA]
And the TraceEntry.getTargets() returns [tableA]
```

#### Scenario: Create trace entry with multiple sources
```
Given source EObjects "entityA" and "entityB"
And target EObjects "tableA", "tableB", "tableC"
When I create a TraceEntry with sources=[entityA, entityB] and targets=[tableA, tableB, tableC]
Then the TraceEntry.getSources() returns [entityA, entityB]
And the TraceEntry.getTargets() returns [tableA, tableB, tableC]
And the TraceEntry.getSource() returns "entityA" (first source)
```

#### Scenario: Create trace entry with rule metadata
```
Given a trace entry
When I set ruleName="Entity2Table", discriminator="create", primary=true
Then the TraceEntry.getRuleName() returns "Entity2Table"
And the TraceEntry.getDiscriminator() returns "create"
And the TraceEntry.isPrimary() returns true
```

---

### Requirement: TRACE-002
**Title**: TraceFormat Enumeration

The system shall provide a `TraceFormat` enum to identify supported trace formats.

#### Scenario: Detect format from JSON file
```
Given a filename "trace.json"
When I call TraceFormat.detect(filename)
Then the result is TraceFormat.ZETA_JSON
```

#### Scenario: Detect format from XMI file
```
Given a filename "trace.xmi"
When I call TraceFormat.detect(filename)
Then the result is TraceFormat.ETL_XMI
```

#### Scenario: Default format for unknown extension
```
Given a filename "trace.unknown"
When I call TraceFormat.detect(filename)
Then the result is TraceFormat.ETL_XMI (default)
```

---

### Requirement: TRACE-003
**Title**: Zeta JSON Trace Loading

The system shall load transformation traces from Zeta JSON format.

#### Scenario: Load trace with single source and target
```
Given a JSON file containing:
  {
    "traceEntries": [{
      "ruleName": "Entity2Table",
      "source": {"type": "EntityType", "id": "entity-1", "name": "Customer"},
      "target": {"type": "Table", "id": "table-1", "name": "Customer"},
      "primary": true
    }]
  }
And ResourceSets containing elements where EObject.eGet("id") matches "entity-1" and "table-1"
When I load the trace with ZetaTraceLoader
Then I get 1 TraceEntry
And the entry has source resolved via 'id' attribute matching "entity-1"
And the entry has target resolved via 'id' attribute matching "table-1"
And the entry has ruleName="Entity2Table"
And the entry has primary=true
```

#### Scenario: Element resolution uses 'id' EAttribute
```
Given a JSON trace entry with source.id="my-entity"
And a ResourceSet containing an EObject with eGet(eClass.getEStructuralFeature("id")) == "my-entity"
When I load the trace
Then the source is resolved to that EObject
```

#### Scenario: Load trace with multiple sources
```
Given a JSON file containing:
  {
    "traceEntries": [{
      "ruleName": "Merge2Table",
      "sources": [
        {"type": "Entity", "id": "entity-1"},
        {"type": "Extension", "id": "ext-1"}
      ],
      "target": {"type": "Table", "id": "table-1"}
    }]
  }
And ResourceSets containing elements with matching IDs
When I load the trace with ZetaTraceLoader
Then I get 1 TraceEntry with 2 sources and 1 target
```

#### Scenario: Load trace with discriminator
```
Given a JSON file containing:
  {
    "traceEntries": [{
      "ruleName": "Relation2Op",
      "source": {"type": "Relation", "id": "rel-1"},
      "target": {"type": "Operation", "id": "op-1"},
      "discriminator": "create"
    }]
  }
When I load the trace with ZetaTraceLoader
Then the entry has discriminator="create"
```

#### Scenario: Group entries by source + ruleName + discriminator
```
Given a JSON file containing:
  {
    "traceEntries": [
      {"source": {"id": "entity-1"}, "target": {"id": "table-1"}, "ruleName": "Rule1"},
      {"source": {"id": "entity-1"}, "target": {"id": "column-1"}, "ruleName": "Rule2"},
      {"source": {"id": "entity-1"}, "target": {"id": "index-1"}, "ruleName": "Rule1"},
      {"source": {"id": "entity-2"}, "target": {"id": "table-2"}, "ruleName": "Rule1"}
    ]
  }
When I load the trace with ZetaTraceLoader
Then I get 3 TraceEntry objects (grouped by source + ruleName)
And entry for "entity-1" + "Rule1" has targets ["table-1", "index-1"]
And entry for "entity-1" + "Rule2" has targets ["column-1"]
And entry for "entity-2" + "Rule1" has targets ["table-2"]
```

#### Scenario: Entries with different discriminators stay separate
```
Given a JSON file containing:
  {
    "traceEntries": [
      {"source": {"id": "rel-1"}, "target": {"id": "op-create"}, "ruleName": "Rel2Op", "discriminator": "create"},
      {"source": {"id": "rel-1"}, "target": {"id": "op-delete"}, "ruleName": "Rel2Op", "discriminator": "delete"}
    ]
  }
When I load the trace with ZetaTraceLoader
Then I get 2 TraceEntry objects (different discriminators)
And entry with discriminator="create" has target "op-create"
And entry with discriminator="delete" has target "op-delete"
```

---

### Requirement: TRACE-004
**Title**: Zeta JSON Trace Saving

The system shall save transformation traces to Zeta JSON format.

#### Scenario: Save trace entries to JSON
```
Given a list of TraceEntry objects
When I save with ZetaTraceLoader
Then the output is valid JSON
And contains "traceEntries" array
And contains "entryCount" matching the number of entries
And contains "timestamp" with current time
```

#### Scenario: Round-trip JSON trace
```
Given a list of TraceEntry objects
When I save to JSON and then load from JSON
Then the loaded entries match the original entries
```

---

### Requirement: TRACE-005
**Title**: ETL XMI Trace Loading (Simplified)

The system shall load transformation traces from ETL XMI format without dynamic metamodel creation.

#### Scenario: Load ETL trace with single source
```
Given an XMI file containing:
  <trace:Trace sourceUri="psm:model#entity-1">
    <targetUri>asm:model#table-1</targetUri>
    <targetUri>asm:model#table-2</targetUri>
  </trace:Trace>
And ResourceSets containing elements with matching URI fragments
When I load the trace with EtlTraceLoader
Then I get 1 TraceEntry
And the entry has 1 source and 2 targets
```

#### Scenario: Load multiple ETL trace entries
```
Given an XMI file with 3 Trace elements
When I load the trace with EtlTraceLoader
Then I get 3 TraceEntry objects
```

#### Scenario: ETL resolution tries URI fragment first
```
Given an XMI trace with sourceUri="psm:model#_abc123"
And a ResourceSet where resource.getEObject("_abc123") returns an EObject
When I load the trace with EtlTraceLoader
Then the source is resolved via URI fragment lookup
```

#### Scenario: ETL resolution falls back to ID attribute
```
Given an XMI trace with sourceUri="psm:model#entity-customer"
And a ResourceSet where resource.getEObject("entity-customer") returns null
And an EObject with eGet("id") == "entity-customer" exists
When I load the trace with EtlTraceLoader
Then the source is resolved via ID attribute fallback
```

---

### Requirement: TRACE-006
**Title**: ETL XMI Trace Saving

The system shall save transformation traces to ETL XMI format.

#### Scenario: Save trace entries to XMI
```
Given a list of TraceEntry objects
When I save with EtlTraceLoader
Then the output is valid XMI
And contains trace:Trace elements
And each Trace has sourceUri attribute
And each Trace has targetUri child elements
```

---

### Requirement: TRACE-007
**Title**: Unified Trace Loading

The system shall provide unified methods to load traces with format detection.

#### Scenario: Load trace with explicit format
```
Given a trace file and TraceFormat.ZETA_JSON
When I call TransformationTraceLoader.loadTrace(input, format, resourceSets)
Then the appropriate loader is used
And trace entries are returned
```

#### Scenario: Load trace with auto-detection
```
Given a trace file "trace.json"
When I call TransformationTraceLoader.loadTrace(file, resourceSets)
Then the format is detected as ZETA_JSON
And the trace is loaded successfully
```

---

### Requirement: TRACE-008
**Title**: Format Conversion Utilities

The system shall provide utilities to convert between trace formats.

#### Scenario: Convert to legacy format
```
Given a list of TraceEntry with multiple sources
When I call TransformationTraceLoader.toLegacyFormat(entries)
Then I get Map<EObject, List<EObject>>
And each first source is a key
And all targets are in the value list
```

#### Scenario: Convert to multi-source format
```
Given a list of TraceEntry objects
When I call TransformationTraceLoader.toMultiSourceFormat(entries)
Then I get Map<List<EObject>, List<EObject>>
And entries with same sources are grouped
```

#### Scenario: Convert from legacy format
```
Given a Map<EObject, List<EObject>>
When I call TransformationTraceLoader.fromLegacyFormat(map)
Then I get List<TraceEntry>
And each entry has 1 source and multiple targets
```

---

### Requirement: TRACE-009
**Title**: Multi-Source Trace Interface Extension

The TransformationTrace interface shall support multi-source trace retrieval.

#### Scenario: Get trace entries list
```
Given a TransformationTrace implementation
When I call getTraceEntries()
Then I get List<TraceEntry> with all mappings
```

#### Scenario: Get multi-source trace map
```
Given a TransformationTrace with multi-source entries
When I call getMultiSourceTransformationTrace()
Then I get Map<List<EObject>, List<EObject>>
```

---

## MODIFIED Requirements

### Requirement: TRACE-010
**Title**: Backward Compatible Legacy Methods

Existing methods in TransformationTraceLoader shall be deprecated but continue to work.

#### Scenario: Legacy resolveTransformationTraceAsEObjectMap still works
```
Given ETL trace EObjects loaded via old method
When I call resolveTransformationTraceAsEObjectMap(traceEntries, resourceSets)
Then I get the same result as before
And a deprecation warning is logged
```

---

## Error Handling

### Requirement: TRACE-011
**Title**: Fail-Fast Error Handling

The system shall fail fast on trace loading errors with clear exception messages.

#### Scenario: Handle malformed JSON
```
Given an invalid JSON file
When I attempt to load with ZetaTraceLoader
Then a TraceLoadException is thrown
And the message indicates JSON parsing error
And the message includes the file location if available
```

#### Scenario: Handle unresolved element reference (FAIL FAST)
```
Given a trace file with element ID "unknown-id"
And no matching element in ResourceSets (no EObject with id="unknown-id")
When I load the trace
Then a TraceLoadException is thrown
And the message includes "Element not found: unknown-id"
And the message includes the trace entry context (e.g., ruleName)
```

#### Scenario: Handle missing 'id' attribute on EObject
```
Given a trace file referencing element ID "entity-1"
And ResourceSets containing EObjects without 'id' EAttribute
When I load the trace
Then a TraceLoadException is thrown
And the message indicates no matching element found
```

#### Scenario: Handle empty trace file
```
Given an empty trace file (valid JSON/XMI with zero entries)
When I load the trace
Then an empty List<TraceEntry> is returned
And no exception is thrown
```

---

## Cross-References

- **TransformationTraceService**: Uses TraceEntry for trace management
- **TransformationMode**: Determines which format is likely (ETL vs Zeta)
- **judo-zeta TransformationTrace**: Source of Zeta JSON format specification
