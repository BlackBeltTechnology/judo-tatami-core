# Transformation Trace Specification

## Purpose

Tracks model-to-model transformation events in the JUDO platform, enabling navigation of trace hierarchies (ascendant and descendant lookups) across chained transformations. Managed as an OSGi Declarative Services component backed by Eclipse EMF.

## Architecture

The transformation trace subsystem centers on the `TransformationTrace` interface, which captures source models, target model, and a bidirectional `EObject → List<EObject>` trace map. `TransformationTraceServiceImpl` (OSGi @Component) maintains a `ConcurrentMap<String, List<TransformationTrace>>` indexed by model name and uses `TransformationTraceTracker` (an OSGi `ServiceTracker`) to dynamically discover `TransformationTrace` service registrations. `TransformationTraceLoader` provides static utilities for creating and resolving EMF-based trace resources using a pseudo trace meta model. `TransformationTraceTreeElement` decorates `TransformationTrace` with hierarchical parent pointers. `AbstractModelTracker<T>` and `AbstractModelPairTracker<T1,T2>` provide reusable OSGi service tracker bases for model services.

## Requirements

### Requirement: TransformationTrace SHALL expose source and target model metadata

A `TransformationTrace` instance must provide access to all source models (by type and as a list), the single target model, associated `ResourceSet` and `URI` references, the trace type, trace name, model name, and model version.

#### Scenario: Retrieve source model by type
- **GIVEN** a `TransformationTrace` with source models of types `A` and `B`
- **WHEN** `getSourceModel(A.class)` is called
- **THEN** the source model instance of type `A` is returned

#### Scenario: Retrieve target model metadata
- **GIVEN** a `TransformationTrace` with a target model
- **WHEN** `getTargetModel()`, `getTargetResourceSet()`, and `getTargetURI()` are called
- **THEN** the correct target model, resource set, and URI are returned

### Requirement: TransformationTrace SHALL provide a bidirectional EObject trace map

The `getTransformationTrace()` method must return a `Map<EObject, List<EObject>>` mapping source EObjects to their corresponding target EObjects.

#### Scenario: Query trace map for a source element
- **GIVEN** a transformation that mapped source element `s1` to target elements `[t1, t2]`
- **WHEN** `getTransformationTrace().get(s1)` is called
- **THEN** the list `[t1, t2]` is returned

### Requirement: TransformationTraceService SHALL support dynamic registration and removal

The service must accept `add(TransformationTrace)` and `remove(TransformationTrace)` calls, indexing traces by `getModelName()`.

#### Scenario: Add a transformation trace
- **GIVEN** a `TransformationTraceService` with no registered traces
- **WHEN** `add(trace)` is called with a trace having `modelName = "myModel"`
- **THEN** the trace is retrievable via hierarchy queries for `"myModel"`

#### Scenario: Remove a transformation trace
- **GIVEN** a `TransformationTraceService` with a registered trace for `"myModel"`
- **WHEN** `remove(trace)` is called
- **THEN** the trace is no longer available for hierarchy queries

### Requirement: TransformationTraceService SHALL navigate trace hierarchies

The service must support ascendant lookups (tracing a target EObject back through chained transformations to its source) and descendant lookups (tracing a source EObject forward to all derived targets).

#### Scenario: Find root ascendant of an element
- **GIVEN** a chain of traces: `A → B → C` registered for model `"m"`
- **WHEN** `getRootAscendantOfInstance("m", elementInC)` is called
- **THEN** the original element in model `A` (the root source) is returned

#### Scenario: Find ascendant by specific model type
- **GIVEN** a chain of traces: `A → B → C` registered for model `"m"`
- **WHEN** `getAscendantOfInstanceByModelType("m", B.class, elementInC)` is called
- **THEN** the corresponding element in model `B` is returned

#### Scenario: Find all descendants of an element
- **GIVEN** a chain of traces: `A → B → C` registered for model `"m"`
- **WHEN** `getAllDescendantOfInstance("m", elementInA)` is called
- **THEN** a map of `TransformationTrace → List<EObject>` is returned containing descendant elements in `B` and `C`

### Requirement: TransformationTraceServiceImpl SHALL use URI fragment comparison for EObject equality

EObject equality in trace lookups must be determined by comparing `eResource().getURI()` and `eResource().getURIFragment(eObject)`, not by Java object identity.

#### Scenario: Match EObjects from different ResourceSets
- **GIVEN** two EObjects from different ResourceSets that represent the same logical element (same URI and fragment)
- **WHEN** a trace lookup is performed
- **THEN** they are treated as equal

### Requirement: TransformationTraceServiceImpl SHALL activate as an OSGi component

The implementation must use `@Component(service = TransformationTraceService.class)` and open a `TransformationTraceTracker` on `@Activate`, closing it on `@Deactivate`.

#### Scenario: OSGi activation
- **GIVEN** an OSGi container with the bundle installed
- **WHEN** the component is activated with a `BundleContext`
- **THEN** a `TransformationTraceTracker` is opened to dynamically discover `TransformationTrace` service registrations

### Requirement: TransformationTraceLoader SHALL create and resolve trace model resources

The loader must provide static methods to create EMF `ResourceSet` and `Resource` instances for the pseudo trace meta model (namespace `http://www.blackbelt.hu/meta/trasformation/trace`), and to resolve `EObject` trace entries against target `ResourceSet` instances.

#### Scenario: Resolve trace entries
- **GIVEN** a list of trace `EObject` entries and a list of target `ResourceSet` instances
- **WHEN** `resolveTransformationTraceAsEObjectMap(entries, resourceSets)` is called
- **THEN** a `Map<EObject, List<EObject>>` is returned with resolved target element references

### Requirement: AbstractModelTracker SHALL track OSGi service registrations

The abstract tracker must maintain a `ConcurrentMap<ServiceReference, T>` cache and call `install(T)` / `uninstall(T)` when services are added or removed.

#### Scenario: Service registration
- **GIVEN** an OSGi `BundleContext` with the tracker open
- **WHEN** a new service of the tracked type is registered
- **THEN** `install(instance)` is called and the instance is cached

#### Scenario: Service unregistration
- **GIVEN** a tracked service instance in the cache
- **WHEN** the service is unregistered
- **THEN** `uninstall(instance)` is called and the instance is removed from the cache

### Requirement: AbstractModelPairTracker SHALL install when both paired services arrive

The pair tracker must wait until both `T1` and `T2` services with matching names are available before calling `install(T1, T2)`.

#### Scenario: Both services arrive
- **GIVEN** a pair tracker for types `T1` and `T2` with name extractors
- **WHEN** both a `T1` and `T2` service with the same extracted name are registered
- **THEN** `install(t1, t2)` is called

#### Scenario: Only one service arrives
- **GIVEN** a pair tracker for types `T1` and `T2`
- **WHEN** only a `T1` service is registered (no matching `T2`)
- **THEN** `install()` is NOT called
