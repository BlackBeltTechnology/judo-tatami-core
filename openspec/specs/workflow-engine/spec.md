# Workflow Engine Specification

## Purpose

Provides a composable workflow engine for orchestrating transformation pipelines. Work units can be composed into sequential, parallel, conditional, and repeat flows, with arbitrary nesting enabled by `WorkFlow` extending `Work`.

## Architecture

The workflow engine has three layers: **work** (the unit of execution), **flow** (composition patterns), and **engine** (the top-level runner). `Work` extends `Callable<WorkReport>` and returns a `DefaultWorkReport` with `WorkStatus` (COMPLETED or FAILED). `WorkFlow` extends `Work`, making any flow usable as a work unit inside another flow. Four built-in flow types (`SequentialFlow`, `ParallelFlow`, `ConditionalFlow`, `RepeatFlow`) extend `AbstractWorkFlow` and are constructed via inner `Builder` classes with static factory methods. `WorkFlowEngine` (implemented by `WorkFlowEngineImpl`, built via `WorkFlowEngineBuilder`) runs the top-level workflow. `AbstractTransformationWork` provides a template method bridging the workflow engine to the transformation trace subsystem via `TransformationContext` and `MetricsCollector`.

## Requirements

### Requirement: Work SHALL be a named callable that returns a WorkReport

Every `Work` implementation must provide a unique `getName()` within its workflow and a `call()` method returning a `WorkReport` with a `WorkStatus` and optional `Throwable` error.

#### Scenario: Successful work execution
- **GIVEN** a `Work` implementation that completes normally
- **WHEN** `call()` is invoked
- **THEN** a `WorkReport` with `WorkStatus.COMPLETED` is returned

#### Scenario: Failed work execution
- **GIVEN** a `Work` implementation that catches an internal exception
- **WHEN** `call()` is invoked
- **THEN** a `WorkReport` with `WorkStatus.FAILED` and the exception as error is returned

### Requirement: Work implementations SHALL catch exceptions and finish in finite time

Work implementations must not let exceptions propagate from `call()` — they must catch them and return `FAILED`. Work must complete in a bounded amount of time.

#### Scenario: Exception handling
- **GIVEN** a `Work` implementation where the internal logic throws an exception
- **WHEN** `call()` is invoked
- **THEN** the exception is caught and a `WorkReport` with `FAILED` status and the exception is returned (no exception propagates)

### Requirement: SequentialFlow SHALL execute works in order and stop on failure

A `SequentialFlow` must execute its list of works in insertion order. If any work returns `FAILED`, subsequent works are skipped and the flow returns the failed report.

#### Scenario: All works succeed
- **GIVEN** a `SequentialFlow` with works `[w1, w2, w3]`
- **WHEN** all works return `COMPLETED`
- **THEN** all three works are executed in order and the flow returns `COMPLETED`

#### Scenario: Second work fails
- **GIVEN** a `SequentialFlow` with works `[w1, w2, w3]`
- **WHEN** `w2` returns `FAILED`
- **THEN** `w1` and `w2` are executed, `w3` is skipped, and the flow returns `FAILED`

### Requirement: ParallelFlow SHALL execute works concurrently

A `ParallelFlow` must submit all works to a `ParallelFlowExecutor` for concurrent execution and return a `ParallelFlowReport`.

#### Scenario: All parallel works succeed
- **GIVEN** a `ParallelFlow` with works `[w1, w2, w3]`
- **WHEN** all works return `COMPLETED`
- **THEN** the flow returns `COMPLETED`

#### Scenario: One parallel work fails
- **GIVEN** a `ParallelFlow` with works `[w1, w2, w3]`
- **WHEN** `w2` returns `FAILED` while `w1` and `w3` return `COMPLETED`
- **THEN** the flow returns `FAILED`

### Requirement: ConditionalFlow SHALL branch based on a WorkReportPredicate

A `ConditionalFlow` must first execute a work, then evaluate a `WorkReportPredicate` on its report. If the predicate is satisfied, the `then` work is executed; otherwise, the `otherwise` work is executed (if provided). If the predicate is not satisfied and no `otherwise` is defined, the flow returns `FAILED`.

#### Scenario: Predicate satisfied
- **GIVEN** a `ConditionalFlow` executing `w1` with predicate `COMPLETED`, then `w2`, otherwise `w3`
- **WHEN** `w1` returns `COMPLETED`
- **THEN** `w2` is executed

#### Scenario: Predicate not satisfied with otherwise
- **GIVEN** a `ConditionalFlow` executing `w1` with predicate `COMPLETED`, then `w2`, otherwise `w3`
- **WHEN** `w1` returns `FAILED`
- **THEN** `w3` is executed

#### Scenario: Predicate not satisfied without otherwise
- **GIVEN** a `ConditionalFlow` executing `w1` with predicate `COMPLETED`, then `w2`, no otherwise
- **WHEN** `w1` returns `FAILED`
- **THEN** the flow returns `FAILED`

### Requirement: RepeatFlow SHALL loop work by count or until a predicate

A `RepeatFlow` must execute a work repeatedly either a fixed number of times (via `TimesPredicate`) or until a `WorkReportPredicate` returns `true`.

#### Scenario: Repeat by count
- **GIVEN** a `RepeatFlow` repeating `w1` 3 times
- **WHEN** the flow is executed
- **THEN** `w1.call()` is invoked exactly 3 times

#### Scenario: Repeat until predicate
- **GIVEN** a `RepeatFlow` repeating `w1` until `COMPLETED`
- **WHEN** `w1` returns `FAILED` twice then `COMPLETED`
- **THEN** `w1.call()` is invoked 3 times and the flow returns `COMPLETED`

### Requirement: WorkFlow SHALL be composable as a Work unit

Since `WorkFlow extends Work`, any workflow can be used as a work unit inside another workflow, enabling arbitrary nesting of flows.

#### Scenario: Nested workflow
- **GIVEN** a `SequentialFlow` containing a `ParallelFlow` as one of its work units
- **WHEN** the sequential flow is executed
- **THEN** the parallel flow is executed as a single work step, and its `WorkReport` determines whether the sequential flow continues

### Requirement: All flows SHALL use the Builder pattern with static factory methods

Each flow type (`SequentialFlow`, `ParallelFlow`, `ConditionalFlow`, `RepeatFlow`) must be constructed exclusively via its inner `Builder` class and a static factory method (e.g., `aNewSequentialFlow()`).

#### Scenario: Build a sequential flow
- **WHEN** `SequentialFlow.Builder.aNewSequentialFlow().named("test").execute(w1).then(w2).build()` is called
- **THEN** a `SequentialFlow` named "test" with works `[w1, w2]` is returned

### Requirement: WorkFlowEngine SHALL run a workflow and return its report

The `WorkFlowEngine.run(WorkFlow)` method must execute the given workflow and return the resulting `WorkReport`.

#### Scenario: Run a workflow
- **GIVEN** a `WorkFlowEngine` built via `WorkFlowEngineBuilder.aNewWorkFlowEngine().build()`
- **WHEN** `run(workFlow)` is called
- **THEN** `workFlow.call()` is invoked and its `WorkReport` is returned

### Requirement: AbstractTransformationWork SHALL provide a template method with metrics

The `call()` method must invoke `metricsCollector.invokedTransformation()` before execution, delegate to the abstract `execute()` method, then call `metricsCollector.stoppedTransformation()` with timing and success/failure status. Exceptions from `execute()` must be caught and wrapped in a `FAILED` report.

#### Scenario: Successful transformation work
- **GIVEN** an `AbstractTransformationWork` subclass with a `MetricsCollector`
- **WHEN** `call()` is invoked and `execute()` completes normally
- **THEN** `invokedTransformation(name)` and `stoppedTransformation(name, elapsed, false)` are called, and `COMPLETED` is returned

#### Scenario: Failed transformation work
- **GIVEN** an `AbstractTransformationWork` subclass with a `MetricsCollector`
- **WHEN** `call()` is invoked and `execute()` throws an exception
- **THEN** `stoppedTransformation(name, elapsed, true)` is called and a `FAILED` report with the exception is returned

### Requirement: TransformationContext SHALL provide thread-safe key-value storage

`TransformationContext` must store variables in a `ConcurrentHashMap` accessible via `put(key, value)`, `get(key)`, `get(Class<T>, key)`, and `getByClass(Class<T>)`.

#### Scenario: Store and retrieve by class
- **GIVEN** a `TransformationContext` with model name `"test"`
- **WHEN** `put(myObject)` is called (using the object's class as key)
- **THEN** `getByClass(MyClass.class)` returns `Optional.of(myObject)`

### Requirement: WorkReportPredicate SHALL support composition via constants and TimesPredicate

The interface must provide static constants `ALWAYS_TRUE`, `ALWAYS_FALSE`, `COMPLETED`, `FAILED`, and a `TimesPredicate` inner class for count-based repetition.

#### Scenario: COMPLETED predicate
- **GIVEN** a `WorkReport` with `WorkStatus.COMPLETED`
- **WHEN** `WorkReportPredicate.COMPLETED.apply(report)` is called
- **THEN** `true` is returned

#### Scenario: TimesPredicate
- **GIVEN** a `TimesPredicate.times(3)`
- **WHEN** `apply()` is called 3 times
- **THEN** it returns `false` for the first 2 calls and `true` on the 3rd call
