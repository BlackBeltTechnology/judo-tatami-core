# `WorkFlowEngineImplTest.java`

Unit test plus worked example for `WorkFlowEngineImpl`.

## Covered behaviour

- `run()` — passes a Mockito-mocked `WorkFlow` to `workFlowEngine.run(workFlow)` and
  verifies the engine calls `WorkFlow.call()` exactly once. This is the only true
  unit assertion in the file.
- `composeWorkFlowFromSeparateFlowsAndExecuteIt()` — builds each flow separately:
  `aNewRepeatFlow().named("print foo 3 times").repeat(work1).times(3)`,
  `aNewParallelFlow().execute(work2, work3)`,
  `aNewConditionalFlow().execute(parallelFlow).when(COMPLETED).then(work4)`, then
  `aNewSequentialFlow().execute(repeatFlow).then(conditionalFlow)`. Runs it through
  `aNewWorkFlowEngine().build()` and prints the `WorkReport`.
- `defineWorkFlowInlineAndExecuteIt()` — same graph, expressed as one nested builder
  chain. Exists to document the inline style, not to assert different behaviour.

## Notes for maintainers

The file's own javadoc states the last two tests "are not really unit tests, but
serve as examples" — they assert nothing and pass as long as no exception escapes.
Treat them as documentation of the builder API; a regression in flow semantics will
not fail them.

Inner `static class PrintMessageWork implements Work` prints its message and always
returns `new DefaultWorkReport(WorkStatus.COMPLETED)`, and `getName()` returns the
constant `"print message work"` for every instance. Because no work ever fails, the
`when(COMPLETED)` branch is always taken — the `otherwise` path of `ConditionalFlow`
has no coverage here.
