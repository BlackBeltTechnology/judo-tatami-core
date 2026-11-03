# `src/main/java/hu/blackbelt/judo/tatami/core/workflow/engine` — entry point that runs a `WorkFlow` and hands back its `WorkReport`

| File | Purpose |
|---|---|
| `WorkFlowEngine.java` | Single-method contract callers use to start a flow. Exports `WorkReport run(WorkFlow)`. Engine owns no thread pool and no scheduler — `run` executes on the calling thread and blocks until the whole flow tree finishes, so a caller wanting async execution must submit `run` to its own executor. |
| `WorkFlowEngineBuilder.java` | Only public route to an engine instance. Exports static `aNewWorkFlowEngine()` and `build()`; constructor is `private`, and the returned implementation type is package-private, so callers outside this package can never `new` an engine and must hold the result as `WorkFlowEngine`. Builder carries no state — each `build()` yields a fresh independent engine. |
| `WorkFlowEngineImpl.java` | Package-private `@Slf4j` engine used by `WorkFlowEngineBuilder.build()`. `run` logs `workFlow.getName()` at DEBUG then returns `workFlow.call()` directly. No try/catch and no report wrapping: a `RuntimeException` escaping the flow propagates to the caller instead of surfacing as a `WorkReport` with `WorkStatus.FAILED`, so callers must handle both failure channels. |
