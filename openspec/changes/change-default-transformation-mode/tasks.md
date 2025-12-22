# Implementation Tasks: Change Default TransformationMode

## Overview

This is a minimal, focused change affecting a single constant and its documentation. The tasks are sequenced to ensure validation before and after the change.

## Prerequisites

- [x] User confirms Zeta transformations are production-ready
- [x] User confirms all critical modules support Zeta mode
- [x] User approves the breaking change for implicit default behavior

## Implementation Tasks

### 1. Pre-Change Validation

- [x] Run full test suite to establish baseline (`mvn clean test`)
- [x] Verify all tests pass with current DEFAULT = ETL
- [x] Document any tests that are mode-specific or parameterized

**Validation**: Green test suite establishes correctness baseline.

### 2. Update TransformationMode Constant

**File**: `src/main/java/hu/blackbelt/judo/tatami/core/TransformationMode.java`

- [x] Change line 82: `public static final TransformationMode DEFAULT = ETL;` → `DEFAULT = ZETA;`
- [x] Update JavaDoc comment on line 33: "Uses ETL by default" → "Uses ZETA by default"
- [x] Update JavaDoc comment on line 80: "Currently ETL is the default until Zeta transformations are fully validated." → "ZETA is the default. Use ETL for backward compatibility or when debugging transformation differences."
- [x] Verify JavaDoc comment on line 89 remains accurate: "(currently ZETA)" is now correct

**Validation**: Code compiles successfully.

### 3. Update README Documentation

**File**: `README.md`

- [x] Locate TransformationMode section (around line 375-388)
- [x] Update documentation to reflect ZETA as default
- [x] Add migration note for users upgrading from previous versions
- [x] Include example of explicitly selecting ETL mode

**Validation**: Documentation is clear and accurate.

### 4. Post-Change Testing

- [x] Run full test suite with new DEFAULT = ZETA (`mvn clean test`)
- [x] Verify all tests pass (tests should be mode-agnostic or parameterized)
- [x] Run integration tests if available
- [x] Test system property override: `-Djudo.transformation.mode=ETL` still works

**Validation**: All tests pass; both modes are selectable.

### 5. Update AGENTS.md (Project Documentation)

**File**: `AGENTS.md`

- [x] Update TransformationMode section to reflect new default (N/A - no TransformationMode section exists in AGENTS.md)
- [x] Note the change in project evolution timeline (N/A - no evolution timeline section exists)

**Validation**: Project documentation matches implementation.

### 6. Verify No Unintended Side Effects

- [x] Search codebase for usages of `TransformationMode.DEFAULT`
- [x] Review each usage to ensure ZETA default is appropriate
- [x] Check if any initialization logic assumes ETL

**Validation**: No unexpected dependencies on ETL default.

## Testing Strategy

### Unit Tests
- Existing tests should be mode-agnostic or parameterized with `@EnumSource(TransformationMode.class)`
- No new tests required for this change
- Verify existing tests pass with ZETA default

### Manual Testing
- Create a minimal transformation work without explicit mode setting
- Verify it uses ZETA by default
- Set `-Djudo.transformation.mode=ETL` and verify ETL is used
- Set `.transformationMode(TransformationMode.ZETA)` explicitly and verify it works

### Regression Testing
- Compare transformation outputs between ETL and ZETA modes
- Ensure both produce equivalent results (if equivalence tests exist)

## Rollback Plan

If issues are discovered after deployment:

1. **Immediate**: Users can override with `-Djudo.transformation.mode=ETL`
2. **Code rollback**: Revert the single-line constant change
3. **Document**: Add known issues to README and proposal

## Dependencies

None. This is a self-contained change in tatami-core.

## Estimated Effort

- Code changes: 15 minutes
- Documentation updates: 30 minutes
- Testing: 1 hour
- **Total**: ~2 hours

## Success Metrics

- [x] All tests pass
- [x] Documentation updated
- [x] Both ETL and ZETA modes confirmed working
- [x] System property override verified
- [x] No regressions in transformation behavior
