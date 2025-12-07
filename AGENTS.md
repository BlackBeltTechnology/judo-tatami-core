<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

# Judo Tatami Util - Project Documentation

## Project Overview

**Repository:** BlackBeltTechnology/judo-tatami-util  
**License:** Eclipse Public License 2.0 (EPL-2.0)  
**Java Version:** 21  
**Build System:** Maven with OSGi Bundle Plugin

This is a utility library for the JUDO Tatami transformation framework that provides:
1. **Transformation trace management** - Tools for handling model transformation traces
2. **Epsilon ETL integration** - Utilities for working with Epsilon transformation traces
3. **EMF model utilities** - Helpers for EMF/Ecore model operations

## Directory Structure

```
judo-tatami-util/
├── src/
│   └── main/
│       └── java/
│           └── hu/blackbelt/judo/tatami/util/
│               └── TransformationTraceExtractor.java
├── pom.xml                         # Maven build configuration
├── logback-test.xml               # Test logging configuration
├── .mvn/                          # Maven wrapper configuration
├── .github/                       # GitHub issue templates
└── openspec/                      # OpenSpec change management
```

## Core Components

### TransformationTraceExtractor

The main utility class (`src/main/java/hu/blackbelt/judo/tatami/util/TransformationTraceExtractor.java`) provides:

| Method | Purpose |
|--------|---------|
| `resolveTransformationTraceAsEObjectMap()` | Resolves trace entries from trace model to EObject map |
| `getTransformationTraceFromEtlExecutionContext()` | Extracts trace entries from Epsilon ETL execution |
| `getTraceEObjectMapFromEtlExecutionContext()` | Gets flattened EObject trace map from ETL context |
| `createTraceResourceSet()` | Creates ResourceSet for handling pseudo trace models |
| `createTraceModelResourceFromEObjectMap()` | Saves trace object map as pseudo trace model |
| `createTraceModelResourceFromTraceList()` | Saves trace list to Resource |
| `createTraceModelResource()` | Creates empty trace model Resource |

### Trace Model Structure

The trace metamodel is a pseudo metamodel created on-the-fly with this structure:

```xml
<ecore:EPackage name="trace" nsURI="http:///www.blackbelt.hu/meta/trasformation/trace/{namespace}">
  <eClassifiers xsi:type="ecore:EClass" name="Trace">
    <eStructuralFeatures xsi:type="ecore:EAttribute" name="sourceUri" eType="EString"/>
    <eStructuralFeatures xsi:type="ecore:EAttribute" name="targetUri" upperBound="-1" eType="EString"/>
  </eClassifiers>
</ecore:EPackage>
```

## Technology Stack

### Core Technologies
- **Eclipse Modeling Framework (EMF)** - Model foundation
- **Ecore** - Model definition language  
- **Epsilon Runtime** 2.8.0 - ETL transformation support
- **Judo Tatami Core** - Core transformation framework

### Runtime
- **OSGi Bundle** - Packaged as OSGi bundle via Apache Felix Bundle Plugin
- **SLF4J** 2.0.16 - Logging facade

### Build & Quality
- **Maven** 3.9.4+
- **JaCoCo** 0.8.12 - Code coverage
- **SonarQube** 3.9.1 - Code quality
- **Lombok** 1.18.34 - Annotation processing

## Build Commands

```bash
# Standard build
mvn clean install

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
| `generate-github-asciidoc-diagrams` | Generate documentation diagrams |
| `update-source-code-license` | Update license headers |

## Dependencies

### Main Dependencies
- `hu.blackbelt.judo.tatami:judo-tatami-core` - Core Tatami framework
- `hu.blackbelt.epsilon:epsilon-runtime-execution` - Epsilon runtime
- `org.eclipse.emf:org.eclipse.emf.ecore` - EMF Ecore
- `org.eclipse.emf:org.eclipse.emf.common` - EMF Common
- `org.eclipse.emf:org.eclipse.emf.ecore.xmi` - EMF XMI support
- `com.google.guava:guava` - Google Guava utilities

### Test Dependencies
- JUnit Jupiter 5.9.1
- Hamcrest 2.2
- Mockito 4.8.0

## OSGi Bundle Configuration

The project exports the following package:
```
hu.blackbelt.judo.tatami.util.*
```

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build configuration with bundle plugin |
| `logback-test.xml` | Test logging configuration |
| `.mvn/extensions.xml` | Maven extensions |

## Development Environment

**Required:**
- Java 21 JDK
- Maven 3.9.4+

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** SNAPSHOT-based development (currently 1.0.0-SNAPSHOT)

## Important Notes

1. **Understand EMF/Ecore patterns** before modifying trace handling code
2. **Trace URIs** are based on source and target model URIs - resources must be available with the same URI for trace resolution
3. **Use OpenSpec for significant changes** - See `openspec/AGENTS.md` for proposal workflow

## Related Projects

- `judo-tatami-core` - Core transformation framework this utility depends on
- `epsilon-runtime` - Epsilon execution runtime
