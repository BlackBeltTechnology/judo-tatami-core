# judo-tatami-core

[![Build](https://github.com/BlackBeltTechnology/judo-tatami-core/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/BlackBeltTechnology/judo-tatami-core/actions/workflows/build.yml)

## Introduction

JUDO Tatami Core provides the foundational API and utilities for the JUDO Tatami transformation framework. It includes:

- **Transformation Trace System** - Track model transformations through a pipeline
- **Workflow Engine** - Compose and execute transformation workflows
- **OSGi Integration** - Service tracking and bundle management
- **EMF/Ecore Utilities** - Helpers for model operations

## Project Details

| Attribute | Value |
|-----------|-------|
| **Artifact** | `hu.blackbelt.judo.tatami:judo-tatami-core` |
| **Java Version** | 21 |
| **Packaging** | OSGi Bundle |
| **License** | EPL-2.0 |

## Architecture

```
hu.blackbelt.judo.tatami.core/
├── TransformationTrace              # Core interface for trace events
├── TransformationTraceService       # API for trace pipeline management
├── TransformationTraceServiceImpl   # OSGi service implementation
├── TransformationTraceTracker       # OSGi ServiceTracker for traces
├── TransformationTraceLoader        # Utility for trace persistence
├── TransformationMode               # ETL vs ZETA engine selector
├── AbstractModelTracker<T>          # Generic OSGi model tracker
├── PrettyPrinter                    # EObject debug printing
├── ZipUtil                          # File compression utility
└── workflow/
    ├── engine/
    │   ├── WorkFlowEngine           # Engine interface
    │   ├── WorkFlowEngineImpl       # Simple executor
    │   └── WorkFlowEngineBuilder    # Builder for engine
    ├── flow/
    │   ├── WorkFlow                 # extends Work
    │   ├── SequentialFlow           # Chain execution, stops on failure
    │   ├── ParallelFlow             # Concurrent execution
    │   ├── ConditionalFlow          # Predicate-based branching
    │   └── RepeatFlow               # Loop execution
    └── work/
        ├── Work                     # Callable<WorkReport> interface
        ├── AbstractTransformationWork # Base for transformations
        ├── TransformationContext    # Key-value context holder
        ├── WorkReport               # Execution result interface
        ├── DefaultWorkReport        # Standard implementation
        ├── WorkStatus               # COMPLETED | FAILED
        ├── WorkReportPredicate      # Flow control predicates
        └── MetricsCollector         # Performance tracking
```

## Core Concepts

### Transformation Trace System

The trace system tracks model transformations through a pipeline, enabling traceability from source to target models.

#### Trace Pipeline Architecture

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│   PSM   │────►│   ASM   │────►│  RDBMS  │────►│  Final  │
│  Model  │     │  Model  │     │  Model  │     │  Model  │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
     ▲               │               │               │
     │               ▼               ▼               ▼
     │          ┌─────────┐    ┌─────────┐    ┌─────────┐
     │          │ Trace 1 │    │ Trace 2 │    │ Trace 3 │
     │          └─────────┘    └─────────┘    └─────────┘
     │                                               │
     │              Ascendant Traversal              │
     └───────────────────────────────────────────────┘
                   Descendant Traversal
     ─────────────────────────────────────────────────►
```

#### Supported Trace Formats

| Format | Extension | Description |
|--------|-----------|-------------|
| **ETL XMI** | `.xmi` | Legacy Epsilon ETL trace format |
| **Zeta JSON** | `.json` | New JSON format with multi-source support |

#### Loading Traces

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Trace File     │────►│  TraceLoader     │────►│  TraceEntry[]   │
│  (.json/.xmi)   │     │  (auto-detect)   │     │  with EObjects  │
└─────────────────┘     └────────┬─────────┘     └─────────────────┘
                                 │
                                 ▼
                        ┌──────────────────┐
                        │  ResourceSets    │
                        │  (for resolution)│
                        └──────────────────┘
```

**Unified API:**
```java
// Auto-detect format by file extension
List<TraceEntry> entries = TransformationTraceLoader.loadTrace(
    new File("trace.json"),
    Arrays.asList(sourceResourceSet, targetResourceSet)
);

// Explicit format
List<TraceEntry> entries = TransformationTraceLoader.loadTrace(
    new File("trace.xmi"),
    TraceFormat.ETL_XMI,
    Arrays.asList(sourceResourceSet, targetResourceSet)
);
```

**Element Resolution:**
- **Zeta JSON**: Matches `id` field against EObject's `id` EAttribute
- **ETL XMI**: URI fragment match first, falls back to `id` EAttribute

#### TraceEntry Data Model

```java
// Single source, multiple targets
TraceEntry entry = TraceEntry.builder()
    .source(sourceEObject)
    .target(targetEObject1)
    .target(targetEObject2)
    .ruleName("Entity2Table")
    .build();

// Multi-source (Zeta semantics)
TraceEntry entry = TraceEntry.builder()
    .source(sourceEObject1)
    .source(sourceEObject2)
    .target(mergedTargetEObject)
    .ruleName("MergeEntities")
    .discriminator("type1")
    .primary(true)
    .build();
```

#### Trace File Examples

**Zeta JSON format:**
```json
{
  "traceEntries": [
    {
      "ruleName": "Entity2Table",
      "source": { "type": "Entity", "id": "entity-customer" },
      "target": { "type": "Table", "id": "table-customer" },
      "primary": true
    },
    {
      "ruleName": "MergeEntities",
      "source": { "type": "Entity", "id": "entity-order" },
      "target": { "type": "Table", "id": "table-order" },
      "discriminator": "order-type"
    }
  ],
  "entryCount": 2,
  "timestamp": 1699123456789
}
```

**ETL XMI format:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xmi:XMI xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
    xmlns:trace="http:///www.blackbelt.hu/meta/trasformation/trace/psm2asm">
  <trace:Trace sourceUri="psm:model#_abc123">
    <targetUri>asm:model#_def456</targetUri>
    <targetUri>asm:model#_ghi789</targetUri>
  </trace:Trace>
</xmi:XMI>
```

#### Saving Traces

```java
// Save with auto-detect format
TransformationTraceLoader.saveTrace(entries, new File("trace.json"));

// Save with explicit format
TransformationTraceLoader.saveTrace(entries, new File("trace.xmi"), TraceFormat.ETL_XMI);
```

#### Format Conversion

```java
// Convert to legacy format (for backward compatibility)
Map<EObject, List<EObject>> legacyMap = TransformationTraceLoader.toLegacyFormat(entries);

// Convert from legacy format
List<TraceEntry> entries = TransformationTraceLoader.fromLegacyFormat(legacyMap);

// Convert to multi-source format
Map<List<EObject>, List<EObject>> multiSourceMap = TransformationTraceLoader.toMultiSourceFormat(entries);
```

#### TransformationTraceService

Register and query traces through the service:

```java
// Register traces
TransformationTraceService traceService = new TransformationTraceServiceImpl();
traceService.add(psmToAsmTrace);
traceService.add(asmToRdbmsTrace);
```

**Single-source API (legacy):**
```java
// Get ancestor of specific model type
EObject psmElement = traceService.getAscendantOfInstanceByModelType(
    "myModel", PsmModel.class, finalElement);

// Get root ancestor
EObject root = traceService.getRootAscendantOfInstance("myModel", finalElement);

// Get descendants of specific type
List<EObject> tables = traceService.getDescendantOfInstanceByModelType(
    "myModel", RdbmsModel.class, psmEntity);

// Get full ancestor chain
Map<TransformationTrace, EObject> ancestors =
    traceService.getAllAscendantOfInstance("myModel", finalElement);
```

**Multi-source API (new):**
```java
// Get ALL source ancestors (for multi-source traces)
List<EObject> allPsmSources = traceService.getAscendantsOfInstanceByModelType(
    "myModel", PsmModel.class, finalElement);

// Get all root ancestors
List<EObject> allRoots = traceService.getRootAscendantsOfInstance("myModel", finalElement);

// Get descendants from multiple sources
List<EObject> descendants = traceService.getDescendantsOfInstancesByModelType(
    "myModel", RdbmsModel.class,
    psmEntity1, psmEntity2  // varargs
);

// Get full ancestor map with multi-source support
Map<TransformationTrace, List<EObject>> ancestorMap =
    traceService.getAllAscendantsOfInstanceMultiSource("myModel", finalElement);

// Find trace entries for an element
List<TraceEntry> entries = traceService.getTraceEntriesForInstance("myModel", element);
```

#### Multi-Source Scenario

```
┌─────────┐
│ Entity1 │──┐
└─────────┘  │     ┌─────────────┐
             ├────►│ MergedTable │
┌─────────┐  │     └─────────────┘
│ Entity2 │──┘
└─────────┘

// Zeta trace with multiple sources
{
  "ruleName": "MergeEntities",
  "sources": [
    { "type": "Entity", "id": "entity1" },
    { "type": "Entity", "id": "entity2" }
  ],
  "target": { "type": "Table", "id": "merged-table" }
}

// Query returns both sources
List<EObject> sources = traceService.getAscendantsOfInstanceByModelType(
    "myModel", EntityModel.class, mergedTable);
// Returns: [Entity1, Entity2]
```

#### TransformationTrace Interface

```java
interface TransformationTrace {
    // Model types
    List<Class> getSourceModelTypes();
    Class getTargetModelType();

    // Model instances
    List<Object> getSourceModels();
    Object getTargetModel();

    // ResourceSets
    <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType);
    ResourceSet getTargetResourceSet();

    // Legacy trace map (single source -> multiple targets)
    Map<EObject, List<EObject>> getTransformationTrace();

    // New unified API
    default List<TraceEntry> getTraceEntries();
    default Map<List<EObject>, List<EObject>> getMultiSourceTransformationTrace();

    // Metadata
    String getModelName();
    String getTransformationTraceName();
}
```

**Key features:**
- Traces are indexed by `modelName` in `TransformationTraceServiceImpl`
- Traces form a DAG (directed acyclic graph) from root to leaf models
- Ascendant methods traverse up the trace chain (target → source)
- Descendant methods traverse down the trace chain (source → target)
- Multi-source API returns `List<EObject>` instead of single `EObject`

### Workflow Engine

Builder pattern for composing transformations:

```java
// Sequential execution - stops on first failure
SequentialFlow.Builder.aNewSequentialFlow()
    .named("pipeline")
    .execute(work1)
    .then(work2)
    .then(work3)
    .build();

// Parallel execution - runs all concurrently
ParallelFlow.Builder.aNewParallelFlow()
    .execute(work1, work2, work3)
    .build();

// Conditional branching
ConditionalFlow.Builder.aNewConditionalFlow()
    .execute(checkWork)
    .when(WorkReportPredicate.COMPLETED)
    .then(successWork)
    .otherwise(failureWork)
    .build();
```

### Creating Transformation Work

Extend `AbstractTransformationWork` to create transformation steps:

```java
public class MyTransformationWork extends AbstractTransformationWork {

    public MyTransformationWork(TransformationContext ctx) {
        super(ctx);
    }

    @Override
    public void execute() throws Exception {
        // Get inputs from context
        MyInputModel input = transformationContext
            .getByClass(MyInputModel.class)
            .orElseThrow();

        // Perform transformation
        MyOutputModel output = transform(input);

        // Store in context for next work
        transformationContext.put(output);
    }
}
```

### TransformationMode

Switch between ETL (Epsilon) and ZETA (Java) transformation engines:

```java
public enum TransformationMode {
    ETL,   // Legacy Epsilon ETL scripts
    ZETA;  // New Java-based transformations
}

// Override via system property
// -Djudo.transformation.mode=ZETA
TransformationMode mode = TransformationMode.fromSystemProperty();
```

## EMF/Ecore Patterns

### EObject Equality

The project uses URI-fragment based equality for performance:

```java
// Efficient equality check using URI fragments
public static boolean equals(EObject o1, EObject o2) {
    Resource r1 = o1.eResource();
    Resource r2 = o2.eResource();

    if (r1 != null && r2 != null && Objects.equals(r1.getURI(), r2.getURI())) {
        return Objects.equals(r1.getURIFragment(o1), r2.getURIFragment(o2));
    }
    return EcoreUtil.equals(o1, o2);
}
```

### Trace Model Structure (Legacy)

The legacy ETL XMI trace format uses a pseudo-metamodel created on-the-fly. The new unified API (`TransformationTraceLoader`) handles this automatically - see [Transformation Trace System](#transformation-trace-system) for details.

```xml
<ecore:EPackage name="trace"
    nsURI="http:///www.blackbelt.hu/meta/trasformation/trace/{namespace}">
  <eClassifiers xsi:type="ecore:EClass" name="Trace">
    <eStructuralFeatures name="sourceUri" eType="EString"/>
    <eStructuralFeatures name="targetUri" upperBound="-1" eType="EString"/>
  </eClassifiers>
</ecore:EPackage>
```

## OSGi Integration

### Service Registration

```java
@Component(service = TransformationTraceService.class)
public class TransformationTraceServiceImpl implements TransformationTraceService {

    @Activate
    public void activate(BundleContext bundleContext) {
        openTracker(bundleContext);
    }

    @Deactivate
    public void deactivate() {
        closeTracker();
    }
}
```

### Model Tracking Pattern

```java
public abstract class AbstractModelTracker<T> {
    public abstract void install(T instance);
    public abstract void uninstall(T instance);
    public abstract Class<T> getModelClass();
}
```

## Build

```bash
# Standard build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# With Maven wrapper
./mvnw clean install
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `sign-artifacts` | GPG signing for release |
| `release-central` | Maven Central deployment |
| `release-judong` | Internal Judo repository |
| `release-dummy` | Local file system deployment for testing |

## Dependencies

| Dependency | Purpose |
|------------|---------|
| `org.eclipse.emf.ecore` | EMF model foundation |
| `org.eclipse.emf.ecore.xmi` | XMI serialization |
| `com.google.guava` | Collections, Preconditions |
| `com.google.code.gson` | JSON trace format parsing |
| `org.projectlombok` | @Slf4j, @Builder, @Getter |
| `org.osgi.core` | OSGi runtime |

## Testing

```java
// JUnit 5 with Mockito
@Test
void testSequentialFlow() {
    Work work1 = Mockito.mock(Work.class);
    Work work2 = Mockito.mock(Work.class);

    SequentialFlow flow = SequentialFlow.Builder.aNewSequentialFlow()
        .execute(work1).then(work2).build();

    flow.call();

    InOrder inOrder = Mockito.inOrder(work1, work2);
    inOrder.verify(work1).call();
    inOrder.verify(work2).call();
}
```

## Exported Package

```
hu.blackbelt.judo.tatami.core.*
```

## Context

This project is a building block of the [judo-community](https://github.com/BlackBeltTechnology/judo-community) aggregator project. In order to better understand how this module fits into our ecosystem, please check the corresponding documentation!

## Contributing to the project

Everyone is welcome to contribute to JUDO! As a starter, please read the corresponding [CONTRIBUTING](CONTRIBUTING.md) guide for details!

## License

This project is licensed under the [Eclipse Public License - v 2.0](https://www.eclipse.org/legal/epl-2.0/).
