# Judo Tatami Core - Project Documentation

## Project Overview

**Repository:** BlackBeltTechnology/judo-tatami-core
**License:** Eclipse Public License 2.0 (EPL-2.0)
**Java Version:** 21
**Build System:** Maven 3.9.4+ with Maven Wrapper (`mvnw`)

1. Provides **transformation trace tracking** for model-to-model conversions in the JUDO platform, backed by Eclipse EMF and managed via OSGi Declarative Services
2. Implements a **composable workflow engine** with sequential, parallel, conditional, and repeat flow types that can be nested arbitrarily
3. Includes OSGi **service tracker abstractions** (`AbstractModelTracker`, `AbstractModelPairTracker`) for dynamic model service binding
4. Offers **utility classes** for EMF pretty-printing, ZIP compression, ANSI terminal coloring, EMap wrapping, and caching input streams
5. Part of the [judo-community](https://github.com/BlackBeltTechnology/judo-community) aggregator ecosystem — consumed by other "tatami" modules

## Directory Structure

```
judo-tatami-core/
├── src/
│   ├── main/java/hu/blackbelt/judo/tatami/core/
│   │   ├── *.java                          # Transformation trace + utilities
│   │   └── workflow/
│   │       ├── engine/                     # WorkFlowEngine, builder
│   │       ├── flow/                       # SequentialFlow, ParallelFlow, ConditionalFlow, RepeatFlow
│   │       └── work/                       # Work, WorkReport, TransformationContext, predicates
│   └── test/java/hu/blackbelt/judo/tatami/core/
│       ├── TransformationTraceServiceImplTest.java
│       └── workflow/flow/                  # Flow tests (SequentialFlowTest, ParallelFlowTest, etc.)
├── documentation/
│   └── workflow.md                         # Workflow engine API guide with examples
├── .github/
│   ├── workflows/                          # CI/CD GitHub Actions
│   └── CIFLOW.md                           # CI/CD flow documentation
├── pom.xml                                 # Single-module OSGi bundle POM
├── mvnw / mvnw.cmd                        # Maven wrapper
└── logback-test.xml                        # Test logging configuration
```

## Core Modules

This is a **single-module project** (no Maven submodules). The code is organized into two functional areas:

### Transformation Trace

| Class / Interface | Type | Purpose |
|---|---|---|
| `TransformationTrace` | Interface | Represents a model conversion event with source/target models, URIs, and trace maps |
| `TransformationTraceService` | Interface | API for querying trace hierarchies — ascendant/descendant lookups by model name and EObject |
| `TransformationTraceServiceImpl` | OSGi @Component | Implementation using ConcurrentMap caching and `TransformationTraceTracker` |
| `TransformationTraceLoader` | Utility | Creates and resolves EMF-based trace models with pseudo trace meta models |
| `TransformationTraceTreeElement` | Decorator | Wraps `TransformationTrace` with hierarchical parent relationships |
| `AbstractModelTracker<T>` | Abstract | OSGi ServiceTracker base for tracking model service registrations |
| `AbstractModelPairTracker<T1,T2>` | Abstract | Tracks paired model services, calling `install(T1,T2)` when both arrive |
| `TransformationTraceTracker` | ServiceTracker | Tracks `TransformationTrace` services and delegates to `TransformationTraceService` |

### Workflow Engine

| Class / Interface | Type | Purpose |
|---|---|---|
| `Work` | Interface | Unit of work — extends `Callable<WorkReport>`, has `getName()` and `call()` |
| `WorkReport` / `DefaultWorkReport` | Interface / Class | Execution result with `WorkStatus` (COMPLETED or FAILED) and optional error |
| `WorkReportPredicate` | @FunctionalInterface | Decision logic for conditional/repeat flows; includes COMPLETED, FAILED, ALWAYS_TRUE, ALWAYS_FALSE constants and `TimesPredicate` |
| `WorkFlow` | Interface | Extends `Work` — makes workflows composable (a workflow is also a work unit) |
| `AbstractWorkFlow` | Abstract | Base class providing `name` field for all flow implementations |
| `SequentialFlow` | Flow | Executes works in order; stops on first failure |
| `ParallelFlow` | Flow | Executes works concurrently via `ParallelFlowExecutor`; returns `ParallelFlowReport` |
| `ConditionalFlow` | Flow | Branches on `WorkReportPredicate` — then/otherwise paths |
| `RepeatFlow` | Flow | Loops work a fixed number of times or until a predicate is satisfied |
| `WorkFlowEngine` / `WorkFlowEngineImpl` | Interface / Class | Top-level entry point: `run(WorkFlow) → WorkReport` |
| `WorkFlowEngineBuilder` | Builder | `aNewWorkFlowEngine().build()` factory |
| `AbstractTransformationWork` | Abstract | Template method: wraps `execute()` with metrics collection and error handling |
| `TransformationContext` | Context | Shared key-value store for transformation workflows with type-safe `get(Class<T>)` |
| `MetricsCollector` | Interface | Callbacks for `invokedTransformation()` and `stoppedTransformation()` timing |

### Utilities

| Class | Purpose |
|---|---|
| `ZipUtil` | Recursively compresses files into ZIP archives |
| `EMapWrapper<K,V>` | Adapts EMF `EMap` to standard `java.util.Map` interface |
| `PrettyPrinter` | Recursively pretty-prints EMF `EObject` hierarchies |
| `AnsiColor` | ANSI terminal color utilities (respects `disableJudoAnsiColors` system property) |
| `CachingInputStream` | `BufferedInputStream` subclass that allows reading after close via reset |

## Technology Stack

### Core Technologies
- **Eclipse EMF 4.22** — EObject, ResourceSet, EPackage, URI for model handling
- **OSGi 6.0.0** — Declarative Services (@Component, @Activate, @Deactivate), ServiceTracker
- **Lombok 1.18.34** — @Slf4j, @Getter, @Setter, @Builder, @RequiredArgsConstructor
- **Google Guava 30.0-jre** — Collection utilities
- **SLF4J 2.0.16** — Logging facade (Logback 1.5.12 for tests)

### Build & Quality
- **Maven 3.9.4+** with Maven Wrapper
- **Apache Felix Maven Bundle Plugin 5.1.8** — OSGi bundle packaging
- **JUnit Jupiter 5.9.1** — Unit testing
- **Mockito 4.8.0** — Mocking
- **Hamcrest 2.2** — Assertion matchers
- **JaCoCo 0.8.12** — Code coverage
- **SonarQube** (via sonar-maven-plugin 3.9.1.2184) — Code quality analysis on `develop`
- **Surefire 3.5.1** — Test runner with `--add-opens` for Java 21

## Build Commands

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

### Maven Profiles

| Profile | Purpose |
|---|---|
| `sign-artifacts` | GPG artifact signing via sign-maven-plugin |
| `release-dummy` | Deploy to local `/tmp/` filesystem (testing) |
| `release-judong` | Deploy to JudoNG Nexus (`nexus.judo.technology`) |
| `release-central` | Deploy to Maven Central via OSSRH |
| `generate-github-asciidoc-diagrams` | Generate HTML docs with PlantUML diagrams |
| `update-source-code-license` | Update EPL-2.0 license headers in source files |

## Key Configuration Files

| File | Purpose |
|---|---|
| `pom.xml` | Single-module POM with OSGi bundle packaging, all dependency and plugin config |
| `logback-test.xml` | Logback configuration for test execution |
| `.mvn/extensions.xml` | Maven extensions: wagon-file, wagon-webdav-jackrabbit, buildtime, profile-activator |
| `.github/workflows/build.yml` | Main CI pipeline: build, test, deploy, tag, release |
| `.github/workflows/release.yml` | Manual release trigger creating PRs to master and develop |
| `.github/workflows/merge-pr-tagged.yml` | Merges tagged PRs to master or squashes to develop |

## Development Environment

**Required:**
- Java 21 JDK (Zulu distribution used in CI)
- Maven 3.9.4+ (or use the included `./mvnw` wrapper)
- Git

**Recommended:**
- IDE with Lombok plugin support
- EMF tooling for Ecore model navigation

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** `${revision}` property, currently `1.1.4-SNAPSHOT`
- **Branch naming:** GitFlow — `feature/JNG-xxx_description`, `bugfix/JNG-xxx_description`, `release/x.y.z`
- **Commit rule:** Every commit must reference a JIRA ticket (`JNG-xxx`)
- **CI trigger:** Push to `develop` or PR to `develop`/`master`/`increment/*`/`release/*`
- **Build timeout:** 30 minutes

## Important Notes

1. All flows use the **Builder pattern** with static factory methods (e.g., `SequentialFlow.Builder.aNewSequentialFlow()`) — never use constructors directly
2. `WorkFlow extends Work` — this is the key design decision enabling **arbitrary nesting** of workflows within workflows
3. `TransformationTraceServiceImpl` uses **URI fragment comparison** (not object identity) for EObject equality checks
4. The `AbstractTransformationWork.call()` method is a **template method** — subclasses implement `execute()` while `call()` handles metrics and error wrapping
5. `TransformationContext` uses a `ConcurrentHashMap` — it is thread-safe for parallel flows
6. Work names **must be unique** within a workflow
7. Work implementations **must catch exceptions** and return `WorkStatus.FAILED` — uncaught exceptions break the flow contract
8. OSGi bundle exports `hu.blackbelt.judo.tatami.core.*` — all packages are public

## Related Documentation

- [README.md](README.md) — Project overview and architecture diagrams
- [CONTRIBUTING.md](CONTRIBUTING.md) — Build commands, code structure, submission guidelines
- [documentation/workflow.md](documentation/workflow.md) — Workflow engine API guide with tutorial
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD flow documentation with diagrams
