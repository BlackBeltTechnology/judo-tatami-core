# Tasks: Extend Trace API for Multi-Source Support

## Overview

Implementation tasks for extending the trace API to support multiple sources and Zeta JSON format.

---

## Phase 1: Core Data Model

### Task 1.1: Create TraceEntry Class
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TraceEntry.java`

- [x] Create `TraceEntry` class with Lombok `@Builder` and `@Getter`
- [x] Add `sources: List<EObject>` field
- [x] Add `targets: List<EObject>` field
- [x] Add `ruleName: String` field (optional)
- [x] Add `discriminator: String` field (optional)
- [x] Add `primary: boolean` field
- [x] Add convenience methods: `getSource()`, `getTarget()`
- [x] Add unit tests

**Validation**: Unit test for TraceEntry creation and accessors ✓

---

### Task 1.2: Create TraceFormat Enum
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TraceFormat.java`

- [x] Create `TraceFormat` enum with `ETL_XMI`, `ZETA_JSON`
- [x] Add `detect(String filename)` static method
- [x] Add `getFileExtension()` method
- [x] Add unit tests

**Validation**: Unit test for format detection ✓

---

### Task 1.3: Create TraceLoader Interface
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TraceLoader.java`

- [x] Create `TraceLoader` interface
- [x] Define `loadTrace(InputStream, List<ResourceSet>)` method
- [x] Define `saveTrace(List<TraceEntry>, OutputStream)` method
- [x] Add `TraceLoadException` and `TraceSaveException`

**Validation**: Interface compiles and is documented ✓

---

## Phase 2: Zeta JSON Loader

### Task 2.1: Create ZetaTraceLoader
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/ZetaTraceLoader.java`

- [x] Implement `TraceLoader` interface
- [x] Parse JSON using Gson
- [x] Handle `source` (single) and `sources` (array) fields
- [x] Handle `target` (single) and `targets` (array) fields
- [x] Resolve element IDs against ResourceSets
- [x] Handle missing elements with exception (fail fast)
- [x] Add comprehensive unit tests

**Validation**: Load sample Zeta JSON trace file ✓

---

### Task 2.2: Create ZetaTraceLoader Save Implementation
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/ZetaTraceLoader.java`

- [x] Implement `saveTrace()` method
- [x] Serialize TraceEntry list to JSON
- [x] Include `traceEntries`, `entryCount`, `timestamp` fields
- [x] Format element info with `type`, `id`, `name`
- [x] Add unit tests for round-trip (save then load)

**Validation**: Save trace and verify JSON structure ✓

---

## Phase 3: ETL XMI Loader (Simplified)

### Task 3.1: Create EtlTraceLoader
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/EtlTraceLoader.java`

- [x] Implement `TraceLoader` interface
- [x] Parse XMI directly without dynamic metamodel
- [x] Parse `sourceUri` attribute
- [x] Parse `targetUri` elements
- [x] Resolve URIs against ResourceSets using fragment matching (with ID fallback)
- [x] Convert to TraceEntry format (single source, multiple targets)
- [x] Add unit tests

**Validation**: Load existing ETL trace files ✓

---

### Task 3.2: Create EtlTraceLoader Save Implementation
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/EtlTraceLoader.java`

- [x] Implement `saveTrace()` method
- [x] Write XMI with trace namespace directly
- [x] No EPackage registration required
- [x] Support legacy format (single source → multiple targets)
- [x] Add unit tests

**Validation**: Save trace and verify XMI structure ✓

---

## Phase 4: Update TransformationTraceLoader

### Task 4.1: Add Unified Load Method
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceLoader.java`

- [x] Add `loadTrace(InputStream, TraceFormat, List<ResourceSet>)` method
- [x] Add `loadTrace(File, List<ResourceSet>)` with auto-detection
- [x] Delegate to appropriate loader based on format
- [x] Add unit tests

**Validation**: Load both ETL and Zeta traces via unified method ✓

---

### Task 4.2: Add Format Conversion Utilities
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceLoader.java`

- [x] Add `toLegacyFormat(List<TraceEntry>)` returning `Map<EObject, List<EObject>>`
- [x] Add `toMultiSourceFormat(List<TraceEntry>)` returning `Map<List<EObject>, List<EObject>>`
- [x] Add `fromLegacyFormat(Map<EObject, List<EObject>>)` returning `List<TraceEntry>`
- [x] Add unit tests for conversions

**Validation**: Convert between formats without data loss ✓

---

### Task 4.3: Deprecate Old Methods
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceLoader.java`

- [x] Add `@Deprecated` to `resolveTransformationTraceAsEObjectMap()`
- [x] Add `@Deprecated` to `createTraceResourceSet()`
- [x] Add `@Deprecated` to `getTransformationTraceFromTraceMap()`
- [x] Add `@Deprecated` to `createTraceModelResourceFromEObjectMap()`
- [x] Add Javadoc pointing to replacement methods
- [x] Keep implementations for backward compatibility

**Validation**: Existing tests still pass ✓

---

## Phase 5: Update TransformationTrace Interface

### Task 5.1: Extend TransformationTrace Interface
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTrace.java`

- [x] Add `List<TraceEntry> getTraceEntries()` method (default returns converted legacy)
- [x] Add `Map<List<EObject>, List<EObject>> getMultiSourceTransformationTrace()` method
- [x] Keep `getTransformationTrace()` for backward compatibility
- [x] Update Javadoc

**Validation**: Interface extends without breaking existing implementations ✓

---

## Phase 6: Extend TransformationTraceService

### Task 6.1: Add Multi-Source Ascendant Methods
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceService.java`
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceServiceImpl.java`

- [x] Add `getAscendantsOfInstanceByModelType()` returning `List<EObject>`
- [x] Add `getRootAscendantsOfInstance()` returning `List<EObject>`
- [x] Add `getAllAscendantsOfInstanceMultiSource()` returning `Map<TransformationTrace, List<EObject>>`
- [x] Implement traversal logic for multi-source entries
- [x] Add unit tests

**Validation**: Multi-source ascendant lookups work correctly ✓

---

### Task 6.2: Add Multi-Source Descendant Methods
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceService.java`
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceServiceImpl.java`

- [x] Add `getDescendantsOfInstancesByModelType()` accepting varargs sources
- [x] Add `getAllDescendantsOfInstancesMultiSource()` returning `Map<TransformationTrace, List<EObject>>`
- [x] Implement traversal logic for multi-source entries
- [x] Add unit tests

**Validation**: Multi-source descendant lookups work correctly ✓

---

### Task 6.3: Add TraceEntry Based Methods
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceService.java`
**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationTraceServiceImpl.java`

- [x] Add `getTraceEntriesForInstance(modelName, instance)` method
- [x] Add unit tests

**Validation**: TraceEntry based operations work correctly ✓

---

## Phase 7: Comprehensive Tests

### Task 7.1: Single Source Trace Tests
**File**: `src/test/java/hu/blackbelt/judo/tatami/core/TraceLoaderTest.java`

- [x] Test loading ETL XMI with single source → multiple targets
- [x] Test loading Zeta JSON with single source → single target
- [x] Test conversion to legacy format
- [x] Test round-trip (load → save → load)
- [x] Test TraceService traversal with single source traces

**Validation**: All single source scenarios pass ✓

---

### Task 7.2: Multi-Source Trace Tests
**File**: `src/test/java/hu/blackbelt/judo/tatami/core/MultiSourceTraceServiceTest.java`

- [x] Test loading Zeta JSON with multiple sources
- [x] Test conversion to multi-source format
- [x] Test TraceService traversal with multi-source traces
- [x] Test ancestor/descendant lookup across multiple sources

**Validation**: All multi-source scenarios pass ✓

---

### Task 7.3: Multi-Level Trace Tests
**File**: `src/test/java/hu/blackbelt/judo/tatami/core/MultiSourceTraceServiceTest.java`

- [x] Test traversal over multiple trace maps (pipeline)
- [x] Test root ascendant lookup through multiple levels
- [x] Test descendant lookup through multiple levels
- [x] Test `getAllAscendantOfInstance()` with multi-level traces
- [x] Test `getAllDescendantOfInstance()` with multi-level traces

**Validation**: Multi-level traversal works correctly ✓

---

### Task 7.4: Error Handling Tests
**File**: `src/test/java/hu/blackbelt/judo/tatami/core/TraceLoaderTest.java`

- [x] Test unresolved element references (Zeta)
- [x] Test unresolved element references (ETL)
- [x] Test empty trace entries handling
- [x] Test format detection edge cases

**Validation**: Appropriate exceptions thrown with meaningful messages ✓

---

## Phase 8: Documentation

### Task 8.1: Update AGENTS.md
**File**: `AGENTS.md`

- [x] Document new TraceEntry class
- [x] Document TraceFormat enum
- [x] Document loading both formats
- [x] Add code examples

---

### Task 8.2: Update README.md
**File**: `README.md`

- [x] Documented in AGENTS.md (combined with Task 8.1)

---

## Dependencies

```
Phase 1: Core Data Model
Task 1.1 ─┬─► Task 1.3
          │
Task 1.2 ─┘

Phase 2-3: Loaders (parallel)
Task 1.3 ─┬─► Task 2.1 ─► Task 2.2 (Zeta JSON)
          │
          └─► Task 3.1 ─► Task 3.2 (ETL XMI)

Phase 4: Update TransformationTraceLoader
Tasks 2.2, 3.2 ─► Task 4.1 ─► Task 4.2 ─► Task 4.3

Phase 5: Update Interface
Task 4.3 ─► Task 5.1

Phase 6: Extend Service
Task 5.1 ─┬─► Task 6.1 (Ascendants)
          ├─► Task 6.2 (Descendants)
          └─► Task 6.3 (TraceEntry API)

Phase 7: Tests (parallel after Phase 6)
Tasks 6.x ─┬─► Task 7.1 (Single Source)
           ├─► Task 7.2 (Multi-Source)
           ├─► Task 7.3 (Multi-Level)
           └─► Task 7.4 (Error Handling)

Phase 8: Documentation
Tasks 7.x ─┬─► Task 8.1 (AGENTS.md)
           └─► Task 8.2 (README.md)
```

## Estimated Effort

| Phase | Tasks | Effort | Status |
|-------|-------|--------|--------|
| Phase 1: Core Data Model | 3 | Small | ✅ Complete |
| Phase 2: Zeta JSON Loader | 2 | Medium | ✅ Complete |
| Phase 3: ETL XMI Loader | 2 | Medium | ✅ Complete |
| Phase 4: Update TransformationTraceLoader | 3 | Medium | ✅ Complete |
| Phase 5: Update Interface | 1 | Small | ✅ Complete |
| Phase 6: Extend TransformationTraceService | 3 | Medium | ✅ Complete |
| Phase 7: Comprehensive Tests | 4 | Medium | ✅ Complete |
| Phase 8: Documentation | 2 | Small | ✅ Complete |

**Total**: 20 tasks - **All Complete** ✅

## Implementation Summary

### Files Created
- `TraceEntry.java` - Core data model for trace mappings
- `TraceFormat.java` - Enum for trace format detection
- `TraceLoader.java` - Interface for format-specific loaders
- `TraceLoadException.java` - Exception for load failures
- `TraceSaveException.java` - Exception for save failures
- `ZetaTraceLoader.java` - JSON trace loader/saver
- `EtlTraceLoader.java` - XMI trace loader/saver
- `TraceLoaderTest.java` - Comprehensive unit tests
- `MultiSourceTraceServiceTest.java` - Multi-source service tests

### Files Modified
- `TransformationTraceLoader.java` - Added unified API, deprecated legacy methods
- `TransformationTrace.java` - Added default methods for new API
- `TransformationTraceService.java` - Added multi-source method signatures
- `TransformationTraceServiceImpl.java` - Implemented multi-source methods
- `pom.xml` - Added Gson dependency
- `AGENTS.md` - Updated documentation

### Test Results
- 31 tests total, all passing
- 12 tests in TraceLoaderTest
- 7 tests in MultiSourceTraceServiceTest
- Existing tests continue to pass
