# AGENTS.md — `src/test/java/hu/blackbelt/judo/tatami/core/workflow/engine`

Tests for the workflow engine entry point, doubling as the worked examples of how
flows are assembled from the `workflow.flow` builders.

| File | Purpose |
|---|---|
| `WorkFlowEngineImplTest.java` | Verifies `WorkFlowEngineImpl.run(WorkFlow)` delegates to `WorkFlow.call()`; remaining tests are executable examples of the flow builder API. → see `WorkFlowEngineImplTest.AGENTS.md` |
