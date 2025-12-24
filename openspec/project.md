# Judo Tatami Core - Project Context

## Overview

Judo Tatami Core provides the foundational API and utilities for the JUDO Tatami transformation framework:

- **Transformation Trace System** - Track model transformations through a pipeline
- **Workflow Engine** - Compose and execute transformation workflows
- **OSGi Integration** - Service tracking and bundle management
- **EMF/Ecore Utilities** - Helpers for model operations

## Key Components

### TransformationTrace Interface

Represents a model conversion event tracking elements through the transformation pipeline:

```java
interface TransformationTrace {
    List<Class> getSourceModelTypes();
    List<Object> getSourceModels();
    Class getTargetModelType();
    Object getTargetModel();
    Map<EObject, List<EObject>> getTransformationTrace();  // Source → Target mapping
    String getModelName();
}
```

### TransformationTraceService

API for managing trace pipeline, enabling:
- Ancestor/descendant traversal through trace chain
- Model type-based element resolution
- Multi-level trace graph navigation

### TransformationTraceLoader

Utility for trace persistence using pseudo EMF metamodel:
- Dynamic metamodel creation for XMI serialization
- URI-based source/target resolution
- Current format: Single source → Multiple targets

### TransformationMode

Enum selecting transformation engine:
- `ETL` - Legacy Epsilon ETL scripts
- `ZETA` - New Java-based transformations

## Technology Stack

- Java 21
- Eclipse EMF/Ecore
- OSGi (Apache Felix)
- Lombok
- Google Guava

## Related Projects

- **judo-zeta** - Java transformation framework with JSON trace format
- **epsilon-runtime** - Epsilon ETL execution

## Architectural Constraints

1. Maintain backward compatibility for existing trace consumers
2. Support both ETL and Zeta transformation engines
3. OSGi bundle packaging requirements
4. EMF model-based traceability
