# Spec: Default Transformation Mode

## MODIFIED Requirements

### Requirement: REQ-TM-001

The TransformationMode.DEFAULT constant SHALL equal TransformationMode.ZETA to provide modern Java-based transformations by default.

**Previously:** The constant was set to ETL during the Zeta validation phase.

**Rationale:** Zeta transformations are now production-ready and provide superior developer experience.

#### Scenario: Default mode selection without explicit configuration
- Given: A transformation work is created without specifying transformationMode parameter
- When: The work executes and queries TransformationMode.DEFAULT
- Then: The work SHALL use ZETA transformation engine

#### Scenario: Explicit ETL mode selection
- Given: A transformation work specifies `.transformationMode(TransformationMode.ETL)`
- When: The work executes
- Then: The work SHALL use ETL transformation engine regardless of DEFAULT value

#### Scenario: System property override to ETL
- Given: System property `-Djudo.transformation.mode=ETL` is set
- And: No explicit transformationMode is specified in work parameters
- When: TransformationMode.fromSystemProperty() is called
- Then: The method SHALL return TransformationMode.ETL

#### Scenario: System property override to ZETA
- Given: System property `-Djudo.transformation.mode=ZETA` is set
- When: TransformationMode.fromSystemProperty() is called
- Then: The method SHALL return TransformationMode.ZETA

#### Scenario: Fallback to default when system property is not set
- Given: System property `judo.transformation.mode` is not set
- When: TransformationMode.fromSystemProperty() is called
- Then: The method SHALL return TransformationMode.DEFAULT (which is ZETA)

### Requirement: REQ-TM-002

JavaDoc SHALL indicate ZETA as the default transformation mode and explain that ETL remains available for backward compatibility or debugging purposes.

**Previously:** Documentation indicated ETL as default with Zeta as an override option.

**Rationale:** Documentation must match implementation reality.

#### Scenario: Class-level documentation accuracy
- Given: A developer reads the TransformationMode enum JavaDoc
- When: Reviewing the class-level documentation
- Then: The documentation SHALL state "By default, ZETA (Java-based) transformations are used"

#### Scenario: DEFAULT constant documentation
- Given: A developer inspects TransformationMode.DEFAULT constant
- When: Reading the JavaDoc comment
- Then: The comment SHALL explain ZETA is default and ETL is available for compatibility

#### Scenario: Usage example in JavaDoc
- Given: A developer reads the usage examples in JavaDoc
- When: Reviewing the "Default behavior" example
- Then: The example SHALL indicate ZETA is used by default

## ADDED Requirements

### Requirement: REQ-TM-003

The system SHALL continue to support ETL transformation mode via explicit configuration.

#### Scenario: Legacy code with explicit ETL mode
- Given: Existing code explicitly sets `transformationMode(TransformationMode.ETL)`
- When: The code is executed with the new DEFAULT = ZETA constant
- Then: The transformation SHALL use ETL engine without modification

#### Scenario: Runtime override via system property
- Given: A production deployment cannot modify code
- When: System property `-Djudo.transformation.mode=ETL` is added to JVM arguments
- Then: All transformations SHALL use ETL engine

#### Scenario: Mixed mode usage
- Given: A workflow has multiple transformation steps
- And: Step 1 uses explicit `TransformationMode.ZETA`
- And: Step 2 uses explicit `TransformationMode.ETL`
- And: Step 3 uses default mode
- When: The workflow executes
- Then: Step 1 SHALL use ZETA, Step 2 SHALL use ETL, Step 3 SHALL use ZETA (current default)

### Requirement: REQ-TM-004

Documentation SHALL provide clear guidance for users upgrading from ETL default.

#### Scenario: Developer discovers behavior change
- Given: A developer upgrades to version with ZETA default
- When: Reviewing release notes or README
- Then: Documentation SHALL explain the default mode change and provide ETL override options

#### Scenario: Pin to ETL mode for existing projects
- Given: A project requires ETL behavior to remain unchanged
- When: Consulting migration documentation
- Then: Documentation SHALL provide code examples for explicit ETL mode configuration

## Cross-References

- **Related Capability**: `trace-format-support` (from `extend-trace-api-multi-source` change)
  - ZETA mode produces JSON traces
  - ETL mode produces XMI traces
  - Both formats are supported by unified trace loader

## Non-Functional Requirements

### Performance
- No performance impact: the constant change itself has zero runtime overhead
- ZETA mode may have different performance characteristics than ETL, but this is a property of the modes themselves, not this change

### Compatibility
- Breaking change for consumers relying on implicit default behavior
- Non-breaking for consumers who explicitly specify transformation mode
- System property override provides immediate rollback path

### Testing
- All existing tests SHALL pass with ZETA default
- Tests SHOULD be parameterized with `@EnumSource(TransformationMode.class)` to validate both modes
- System property override SHALL be tested

## Assumptions

1. Zeta transformations are feature-complete and validated
2. Test suite is mode-agnostic or properly parameterized
3. No hidden dependencies on ETL-specific behavior in production code
4. Both ETL and ZETA modes produce equivalent transformation results
