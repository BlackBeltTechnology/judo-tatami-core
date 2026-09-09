# judo-tatami-core — module agent doctrine

## Module purpose

`judo-tatami-core` is the shared execution substrate every JUDO "tatami"
model-to-model transformation is built on: it supplies the workflow engine that
schedules transformation steps, the `TransformationContext` those steps read
their inputs from and publish their outputs into, the engine-selection switch
(`TransformationMode.ETL` — Epsilon `.etl` scripts — versus `ZETA`, the
Java-based transformation framework, or `BOTH` for differential validation), and
the trace machinery (`TransformationTrace`, `TransformationTraceService`,
`TraceLoader` with its `EtlTraceLoader` XMI and `ZetaTraceLoader` JSON
implementations) that records which source EObject produced which target
EObject so a runtime error in a generated model can be walked back to the PSM
element that caused it.

It defines no concrete transformation of its own. Concrete transformation
modules — `judo-tatami-base`, `judo-tatami-jsl`, `judo-tatami-client`,
`judo-tatami-util` — extend `AbstractTransformationWork` and compose their works
through this module's flows; `judo-tatami-pipeline` wires those works into an
end-to-end pipeline. Downstream runtime consumers (`judo-runtime-core`,
`judo-services`, `judo-platform`) depend on it for trace resolution at runtime.

Artifact: `hu.blackbelt.judo.tatami:judo-tatami-core`, version `${revision}`
(currently `1.1.4-SNAPSHOT`), Java 21, packaged as an OSGi bundle by
`maven-bundle-plugin`. Repository: `BlackBeltTechnology/judo-tatami-core`,
licensed EPL-2.0 (secondary GPL-2.0 with Classpath exception). Part of the
[judo-community](https://github.com/BlackBeltTechnology/judo-community)
aggregator ecosystem.

## Reactor map

`pom.xml` declares **no `<modules>`** — `judo-tatami-core` is a single-module
(non-aggregator) reactor producing one OSGi bundle. Nothing to descend into.

<modules>
  (none — single-module OSGi bundle reactor)
</modules>

The bundle's exported surface is `hu.blackbelt.judo.tatami.core.*` — every
package is public API, so a signature change here is a breaking change for every
tatami module downstream. Source is organised in two functional areas under
`src/main/java/hu/blackbelt/judo/tatami/core/`:

- **Trace + model plumbing** (package root) — `TransformationTrace` describes one
  model conversion event (source/target models, URIs, trace maps);
  `TransformationTraceService` answers ascendant/descendant lookups by model name
  and EObject, implemented by the OSGi `@Component`
  `TransformationTraceServiceImpl` over a `ConcurrentMap` cache fed by
  `TransformationTraceTracker`; `TransformationTraceLoader` builds and resolves
  EMF trace models against pseudo trace meta models;
  `TransformationTraceTreeElement` decorates a trace with its parent chain.
  `TraceLoader` abstracts trace persistence in the format named by `TraceFormat`,
  with `EtlTraceLoader` (ETL XMI, StAX-based) and `ZetaTraceLoader` (Zeta JSON,
  Gson-based) implementations exchanging `TraceEntry` records and raising
  `TraceLoadException` / `TraceSaveException`. `AbstractModelTracker<T>` and
  `AbstractModelPairTracker<T1,T2>` are the OSGi `ServiceTracker` bases for
  binding model services dynamically — the pair tracker calls `install(T1,T2)`
  only once both services have arrived.
- **Workflow engine** (`workflow/`) — `work/` holds the unit contract (`Work`
  extends `Callable<WorkReport>`), its result types (`WorkReport`,
  `DefaultWorkReport`, `WorkStatus`, `FailedWork`, `NoOpWork`), the branch
  decision functional interface `WorkReportPredicate` (constants `COMPLETED`,
  `FAILED`, `ALWAYS_TRUE`, `ALWAYS_FALSE`, plus `TimesPredicate`), the
  `AbstractTransformationWork` template base, the shared typed key-value store
  `TransformationContext`, and the `MetricsCollector` timing callbacks
  (`invokedTransformation()` / `stoppedTransformation()`). `flow/` holds
  `WorkFlow` (itself a `Work`), `AbstractWorkFlow`, and the four composable flow
  types — `SequentialFlow`, `ParallelFlow` (with `ParallelFlowExecutor` and
  `ParallelFlowReport`), `ConditionalFlow`, `RepeatFlow`. `engine/` holds the
  entry point `WorkFlowEngine` / `WorkFlowEngineImpl` (`run(WorkFlow) →
  WorkReport`) and its `WorkFlowEngineBuilder` factory.

Supporting EMF and I/O types live beside the trace code: `PrettyPrinter`
recursively renders an `EObject` hierarchy for diagnostics, `EMapWrapper<K,V>`
adapts an EMF `EMap` to `java.util.Map`, `ZipUtil` recursively compresses a
directory tree into a ZIP archive, `CachingInputStream` extends
`BufferedInputStream` so a stream can be re-read after close via reset,
`BufferedSlf4jLogger` buffers `LogEntry` records and implements `Closeable` to
flush them, `LogLevel` names the buffered severity, and `AnsiColor` emits
terminal colour escapes unless the `disableJudoAnsiColors` system property is
set.

Tests mirror this layout under `src/test/java/hu/blackbelt/judo/tatami/core/`
(`TransformationTraceServiceImplTest`, plus `workflow/flow/` flow tests such as
`SequentialFlowTest` and `ParallelFlowTest`), with `logback-test.xml` supplying
test logging configuration. `documentation/workflow.md` is the workflow engine
API guide and tutorial; `.github/CIFLOW.md` documents the CI/CD flow with
diagrams; `.github/workflows/` carries the pipelines — `build.yml` (build, test,
deploy, tag, release), `release.yml` (manual release trigger opening PRs to
master and develop), and `merge-pr-tagged.yml` (merges tagged PRs to master or
squashes to develop). `.mvn/extensions.xml` loads the wagon-file,
wagon-webdav-jackrabbit, buildtime and profile-activator Maven extensions.

## Build commands

Use the bundled Maven wrapper (`./mvnw`), never a repo-root `mvn`.

```bash
# Full build with tests
./mvnw clean install

# Tests only
./mvnw clean test

# Single test class
./mvnw test -Dtest=SequentialFlowTest

# Single test method
./mvnw test -Dtest=SequentialFlowTest#testSequentialFlowExecution

# Skip tests
./mvnw clean install -DskipTests

# Generate JaCoCo coverage report (available at target/site/jacoco/)
./mvnw clean test jacoco:report
```

### Maven profiles

| Profile | Purpose |
|---|---|
| `sign-artifacts` | GPG artifact signing via sign-maven-plugin |
| `release-dummy` | Deploy to local `/tmp/` filesystem (testing) |
| `release-judong` | Deploy to JudoNG Nexus (`nexus.judo.technology`) |
| `release-central` | Deploy to Maven Central via OSSRH |
| `generate-github-asciidoc-diagrams` | Generate HTML docs with PlantUML diagrams |
| `update-source-code-license` | Update EPL-2.0 license headers in source files |

## Technology stack

**Runtime:** Eclipse EMF 4.22 (`EObject`, `ResourceSet`, `EPackage`, `URI`),
OSGi 6.0.0 Declarative Services (`@Component`, `@Activate`, `@Deactivate`,
`ServiceTracker`), Lombok 1.18.34 (`@Slf4j`, `@Getter`, `@Setter`, `@Builder`,
`@RequiredArgsConstructor`), Google Guava 30.0-jre, Gson, SLF4J 2.0.16
(Logback 1.5.12 in tests).

**Build & quality:** Maven 3.9.4+ with wrapper, Apache Felix
`maven-bundle-plugin` 5.1.8 for OSGi packaging, JUnit Jupiter 5.9.1, Mockito
4.8.0, Hamcrest 2.2, JaCoCo 0.8.12, SonarQube via `sonar-maven-plugin`
3.9.1.2184 on `develop`, Surefire 3.5.1 configured with `--add-opens` for Java
21.

**Development environment:** Java 21 JDK (Zulu in CI), Maven 3.9.4+ or the
bundled `./mvnw`, Git. An IDE with Lombok plugin support and EMF tooling for
Ecore navigation is recommended.

## Git workflow

- Main branch: `develop`
- Versioning: the `${revision}` property, currently `1.1.4-SNAPSHOT`
- Branch naming: GitFlow — `feature/JNG-xxx_description`,
  `bugfix/JNG-xxx_description`, `release/x.y.z`
- Commit rule: every commit must reference a JIRA ticket (`JNG-xxx`)
- CI trigger: push to `develop`, or PR to `develop` / `master` / `increment/*` /
  `release/*`
- Build timeout: 30 minutes

## Contracts and invariants

1. All flows are constructed through the **builder pattern** with static factory
   methods (e.g. `SequentialFlow.Builder.aNewSequentialFlow()`) — never call
   constructors directly.
2. `WorkFlow extends Work` — this is the design decision that enables
   **arbitrary nesting** of workflows inside workflows.
3. `TransformationTraceServiceImpl` compares EObjects by **URI fragment**, not by
   object identity.
4. `AbstractTransformationWork.call()` is a **template method**: subclasses
   implement `execute()` while `call()` owns metrics collection and error
   wrapping.
5. `TransformationContext` is backed by a `ConcurrentHashMap` and is therefore
   thread-safe for parallel flows.
6. Work names **must be unique** within a workflow.
7. Work implementations **must catch their exceptions** and return
   `WorkStatus.FAILED` — an uncaught exception breaks the flow contract.
8. The OSGi bundle exports `hu.blackbelt.judo.tatami.core.*`; every package is
   public API.

## Working agreements

1. Read the codebase for the relevant files and think the problem through first.
2. Check in before any major change and let the plan be verified.
3. Give a high-level explanation of what changed at every step.
4. Keep every task and code change as simple as possible — avoid massive or
   complex changes, touch as little code as possible. Everything is about
   simplicity.
5. Maintain a documentation file describing how the architecture works inside and
   out.
6. Never speculate about code you have not opened. If the user references a
   specific file, you MUST read it before answering. Investigate and read the
   relevant files BEFORE answering questions about the codebase; make no claims
   about code before investigating unless you are certain. Give grounded,
   hallucination-free answers.
7. Implement with TDD: write or update tests first to define expected behaviour,
   verify they fail, then write the minimal implementation to make them pass.
8. Apply DRY: extract reusable logic into separate classes, utilities, or
   components; if the same pattern appears in several places, refactor it into a
   shared helper.

## Related documentation

- [README.md](README.md) — project overview and architecture diagrams
- [CONTRIBUTING.md](CONTRIBUTING.md) — build commands, code structure,
  submission guidelines
- [documentation/workflow.md](documentation/workflow.md) — workflow engine API
  guide with tutorial
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD flow documentation with diagrams

<!-- dox-doctrine -->
## Documentation Update Protocol (WRITE discipline)

Per-directory `AGENTS.md` files form a tree. Each directory `AGENTS.md` is the
per-file record for the files in that directory. This module-root `AGENTS.md`
holds doctrine + architecture pointers only — never a per-file index.

**Keep the root lean.** This file loads into every agent turn — every byte costs
tokens on every turn. A verbose root file buries the rules the model must follow
(signal dilution) and measurably degrades adherence; a lean file keeps doctrine
salient. Default assumption: your update does NOT belong in the root — route it
by the table below.

**Route every doc update by kind:**

| Kind of update | Goes in |
|---|---|
| New file in a directory, or its per-file detail / change history | Nearest directory `AGENTS.md`. Add a `` | `<basename>` | <purpose> | `` row, path-alphabetical. |
| Data flow, protocol, architecture rationale | `docs/architecture.md` or a `docs/<topic>.md` |
| End-user / developer setup | `README.md` |
| Cross-cutting rule every agent needs every turn (rare) | this module-root `AGENTS.md` |

**Read before editing (chain walk).** Before editing a file, read the nearest
`AGENTS.md` chain root→leaf so you know the file's recorded purpose, contracts,
and change history. Do not edit blind.

**Update after editing (closeout pass).** After changing a file, update its row
in the nearest directory `AGENTS.md`: find the file's row, update its purpose in
place; if absent, add it in path-alphabetical order. New directory → scaffold
its `AGENTS.md`. One row per file. The purpose carries a one-line summary, key
exported symbols, contracts/invariants, and `See change: <id>` history.

**Row style (caveman).** Short declarative fragments. Drop articles. Subject →
verb → object, present tense. One fact per row. Prefer concrete tokens (paths,
symbols, env vars) over prose. Keep identifiers verbatim.

**Size rule — split an over-large directory `AGENTS.md` file-based.** pi
auto-injects a directory `AGENTS.md` on every turn when cwd sits at/below it, so
an over-large directory `AGENTS.md` is not supported. Split it file-based: a row
exceeding the length threshold promotes to a per-file `<File>.AGENTS.md`
sidecar carrying that file's full detail (including every `See change:`). The
sidecar is pull-only — its name is not `AGENTS.md`, so pi never auto-injects it
— yet it stays search-indexed (`agents` doc_type). The directory `AGENTS.md`
keeps a one-line summary plus a `→ see `<File>.AGENTS.md`` pointer. Rows within
the threshold stay verbatim (lossless).

## Finding docs (READ discipline)

`kb_*` tools are faster and cheaper than raw search — they return a one-line
purpose + key exports per file, not raw bytes. **This fires on the ACTION, not
the intent** — before you `grep`/`rg` for a symbol, `cat`/read a file to learn
what it does, or chase an import, the kb call goes first. It fires **even
mid-task when you already know the file**; knowing the file does not exempt you.
When your reflex is the left column, run the right column instead:

| You're about to… | Do this FIRST instead |
|---|---|
| `grep -rn "SymbolName" src/` — find where a fn / type / const lives | `kb_search --doc-type agents "SymbolName"` — tree indexes key exports per file |
| `grep -rn "feature\|topic" src/` — how does X work / where's X handled | `kb_search "feature topic"` |
| `cat` / read a file just to learn its purpose before editing | `kb agents <path>` — one-line purpose + exports + change history |
| chase imports / callers across files | `kb_neighbors <path\|heading>` |
| read one doc section in full | `kb_get <path> <section>` |

**Fall-through (explicit):** if the kb call returns nothing relevant, `rg` /
source read is allowed — then add the missing directory `AGENTS.md` row per the
WRITE discipline. kb does NOT replace grep; it goes first.
