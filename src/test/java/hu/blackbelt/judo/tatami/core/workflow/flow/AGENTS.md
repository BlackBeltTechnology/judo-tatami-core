# AGENTS.md — `src/test/java/hu/blackbelt/judo/tatami/core/workflow/flow`

Per-flow behavioural tests. Each file pins one flow type's dispatch contract using
Mockito-verified `Work` mocks, so a change in branching, ordering or repeat counts
fails here before it reaches the engine tests.

| File | Purpose |
|---|---|
| `ConditionalFlowTest.java` | Pins `ConditionalFlow` branch selection. `callOnPredicateSuccess` uses `WorkReportPredicate.ALWAYS_TRUE`, `callOnPredicateFailure` uses `ALWAYS_FALSE`; both build via `aNewConditionalFlow().execute(..).when(..).then(..).otherwise(..)`. Asserts the executed work runs exactly once and that exactly one branch fires — the other is `Mockito.never()`. |
| `ParallelFlowExecutorTest.java` | Asserts `ParallelFlowExecutor.executeInParallel(List<Work>)` runs every submitted work and returns one `WorkReport` per work, even when one reports `WorkStatus.FAILED`. Inner `HelloWorldWork` records an `executed` flag and returns `new DefaultWorkReport(status)`; a failing work must not short-circuit its siblings. |
| `ParallelFlowTest.java` | Asserts `ParallelFlow.call()` delegates the whole work list to its injected `ParallelFlowExecutor` rather than running works itself — constructs `new ParallelFlow("pf", works, mockExecutor)` and verifies `executeInParallel(works)` with the identical list. Returned `ParallelFlowReport` must be non-null. |
| `RepeatFlowTest.java` | Pins `RepeatFlow` termination. `testRepeatTimes` asserts `.times(3)` invokes `Work.call()` exactly 3 times; `testRepeatUntil` asserts `.until(WorkReportPredicate.ALWAYS_FALSE)` still runs the work once — the predicate is evaluated after execution, so `until` is do-while, not while. |
| `SequentialFlowTest.java` | Asserts `SequentialFlow` preserves declaration order: builds `execute(work1).then(work2).then(work3)` and verifies each `call()` once through a Mockito `InOrder`. Ordering, not just invocation count, is the contract under test. |
