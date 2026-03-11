# Proposal: Change Default TransformationMode from ETL to ZETA

**Change ID**: `change-default-transformation-mode`
**Status**: Draft
**Created**: 2025-12-22

## Summary

Change the default `TransformationMode` constant from `ETL` to `ZETA` in the tatami-core module to align with the strategic direction toward Java-based transformations and reflect the current state of Zeta transformation maturity.

## Why

The default `TransformationMode` constant currently points to the legacy ETL engine, which creates friction for developers expecting modern Java-based transformations and misaligns with the strategic investment in the Zeta framework. Changing the default to ZETA provides better developer experience by default while maintaining backward compatibility through explicit configuration options.

## Problem Statement

### Current State

The `TransformationMode` enum in `src/main/java/hu/blackbelt/judo/tatami/core/TransformationMode.java` defines:

```java
public static final TransformationMode DEFAULT = ETL;
```

This default was set as a conservative choice during the initial Zeta implementation phase, as documented in the code comment:

> "Currently ETL is the default until Zeta transformations are fully validated."

### Why Change Is Needed

1. **Zeta Maturity**: Zeta transformations have been developed, tested, and validated across multiple transformation modules (esm2psm, psm2asm, asm2script, script2operation)

2. **Strategic Direction**: The project has invested heavily in Zeta framework with:
   - Type-safe Java transformations
   - Better IDE support (debugging, refactoring, navigation)
   - Standard JUnit testing patterns
   - Performance improvements
   - JSON trace format support (recently added via `extend-trace-api-multi-source`)

3. **Documentation Inconsistency**: The JavaDoc example code shows ZETA as the override option, but the actual behavior defaults to the legacy ETL engine

4. **Developer Expectations**: New developers expect the modern Java-based approach to be the default, not an opt-in feature

### Impact Analysis

**Who is affected:**
- Transformation works (Esm2PsmWork, Psm2AsmWork, Asm2ScriptWork, Script2OperationWork) that rely on the default mode
- Developers who don't explicitly set a transformation mode
- System integrators using `-Djudo.transformation.mode` to override

**Breaking Change Assessment:**
- This is a **behavior change** for consumers who rely on implicit defaults
- Consumers who explicitly specify `TransformationMode.ETL` are **not affected**
- System property override `-Djudo.transformation.mode=ETL` continues to work

## Proposed Solution

### Code Change

Update line 82 in `TransformationMode.java`:

```java
// BEFORE
public static final TransformationMode DEFAULT = ETL;

// AFTER
public static final TransformationMode DEFAULT = ZETA;
```

### Documentation Updates

Update JavaDoc comments to reflect the new default:
- Line 33: Change "Uses ETL by default" → "Uses ZETA by default"
- Line 80: Change "Currently ETL is the default..." → "ZETA is the default. Use ETL for backward compatibility."
- Line 89: Change "returns the DEFAULT (ZETA)" → "returns the DEFAULT (currently ZETA)"

### Migration Path for Existing Users

Users who need ETL behavior have three options:
1. **Explicit parameter**: Set `transformationMode(TransformationMode.ETL)` in work parameters
2. **System property**: Use `-Djudo.transformation.mode=ETL` at runtime
3. **Code update**: If pinning to ETL long-term, update code to explicit ETL mode

## Benefits

1. **Modern-by-Default**: New projects get the improved Java-based transformation engine automatically
2. **Aligns with Investment**: Reflects the engineering effort put into Zeta development
3. **Better Developer Experience**: IDE support, debugging, and type safety become the default experience
4. **Performance**: Zeta's performance improvements benefit all new users immediately
5. **Trace Format**: JSON traces (vs XMI) are more debuggable and tool-friendly

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Existing pipelines break | Medium | Clear migration guide; system property override available |
| Undiscovered Zeta bugs | Low | Zeta has extensive test coverage and has been in production use |
| Performance regressions | Low | Zeta performance is equal or better than ETL |
| Trace compatibility | Low | Both formats are supported via unified loader API |

## Assumptions

1. Zeta transformations have been validated and are production-ready
2. All critical transformation modules support Zeta mode
3. The JSON trace format is stable and well-documented
4. There are no known blocking bugs in Zeta implementation

## Questions for Review

Before finalizing this proposal, please confirm:

1. **Validation Status**: Have all Zeta transformations been validated in production or near-production environments?
2. **Test Coverage**: Do we have sufficient equivalence tests comparing ETL and Zeta outputs?
3. **Migration Timeline**: Should we announce this change in advance to give users time to test?
4. **Rollback Plan**: If issues are discovered, is reverting to ETL via system property sufficient?

## Success Criteria

1. `TransformationMode.DEFAULT` constant equals `ZETA`
2. All existing unit tests pass (they should be mode-agnostic or parameterized)
3. Documentation accurately reflects the new default
4. Migration guide is clear and actionable
5. No regressions in transformation correctness or performance
