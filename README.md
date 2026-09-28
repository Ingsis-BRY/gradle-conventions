# Gradle Conventions

Shared Gradle conventions for the projects that make up the architecture.

## Why this repository exists

As the system grows into a multi-project, microservice-oriented architecture, duplicating build and development configuration across repositories makes changes harder to maintain and conventions easier to diverge.

This repository centralizes common build and development configuration into reusable Gradle convention plugins.

Projects can apply the same conventions without copying configuration between repositories, while still keeping repository-specific configuration where it belongs.

The goal is to keep projects consistent and maintainable while allowing each repository to focus on its own domain and application logic.

## Provided conventions

| Convention           | Plugin ID                            | Description                                                              |
| -------------------- | ------------------------------------ | ------------------------------------------------------------------------ |
| Kotlin module        | `com.ingsisbry.kotlin-module`        | Common Kotlin/JVM, testing, Ktlint, Detekt and JaCoCo configuration      |
| Kotlin application   | `com.ingsisbry.kotlin-application`   | Application convention built on top of the Kotlin module convention      |
| Detekt               | `com.ingsisbry.detekt`               | Shared Detekt configuration with support for project-specific overrides  |
| Coverage aggregation | `com.ingsisbry.coverage-aggregation` | Aggregated JaCoCo reports and project-wide coverage verification         |
| Git hooks            | `com.ingsisbry.git-hooks`            | Installs a shared `pre-commit` hook that runs `./gradlew check`          |
| Editor config        | `com.ingsisbry.editor-config`        | Installs the shared `.editorconfig` file when one does not already exist |

## Usage

Projects consume the published conventions through their Gradle `plugins` block.

For example:

```kotlin
plugins {
    id("com.ingsisbry.kotlin-module") version "1.0"
}
```

Multiple conventions can be applied to the same project:

```kotlin
plugins {
    id("com.ingsisbry.kotlin-module") version "1.0"
    id("com.ingsisbry.editor-config") version "1.0"
}
```

Convention plugins can also be wrapped by repository-specific convention plugins.

For example, a project can define:

```kotlin
plugins {
    id("com.ingsisbry.editor-config")
}
```

inside a local convention plugin and then expose its own project-specific plugin ID.

## Conventions

### Kotlin module

```kotlin
plugins {
    id("com.ingsisbry.kotlin-module") version "1.0"
}
```

Provides the common configuration for Kotlin/JVM modules:

* Kotlin JVM
* JDK 21 toolchain
* Ktlint
* Detekt
* JaCoCo
* JUnit Platform
* Kotlin test dependencies
* Sources JAR generation

The convention also configures JaCoCo with version `0.8.15`.

### Kotlin application

```kotlin
plugins {
    id("com.ingsisbry.kotlin-application") version "1.0"
}
```

Builds on top of `com.ingsisbry.kotlin-module` and applies Gradle's `application` plugin.

It also configures the `run` task to use the root project directory as its working directory.

This is useful for applications whose runtime expects files or paths relative to the repository root.

### Detekt

```kotlin
plugins {
    id("com.ingsisbry.detekt") version "1.0"
}
```

Provides a shared default Detekt configuration.

The default configuration is packaged inside the convention plugin, so consuming projects do not need to copy `detekt.yml` into their repositories.

A project can override the default configuration by providing:

```text
config/
└── detekt/
    └── detekt.yml
```

When this file exists, it is used instead of the shared configuration.

The shared convention fails the build when Detekt reports findings.

### Coverage aggregation

```kotlin
plugins {
    id("com.ingsisbry.coverage-aggregation") version "1.0"
}
```

Aggregates JaCoCo coverage across the subprojects of the consuming repository.

It provides:

```text
coverageReport
coverageVerification
```

`coverageReport` generates HTML and XML coverage reports.

`coverageVerification` verifies the aggregated coverage against the configured minimum thresholds.

The current thresholds require at least 80% coverage for:

* Instructions
* Branches
* Lines
* Complexity
* Methods
* Classes

The verification task is also attached to the root `check` task.

### Git hooks

```kotlin
plugins {
    id("com.ingsisbry.git-hooks") version "1.0"
}
```

Provides:

```text
installGitHook
```

The convention packages the default `pre-commit` hook as a plugin resource.

Running:

```bash
./gradlew installGitHook
```

installs it at:

```text
.git/hooks/pre-commit
```

The installed hook runs:

```bash
./gradlew check
```

An existing `pre-commit` hook is not overwritten.

This allows the convention to provide a default hook without requiring every consuming repository to maintain its own copy.

### Editor config

```kotlin
plugins {
    id("com.ingsisbry.editor-config") version "1.0"
}
```

Provides:

```text
installEditorConfig
```

Running:

```bash
./gradlew installEditorConfig
```

installs the shared `.editorconfig` at the root of the consuming repository.

The convention does not overwrite an existing `.editorconfig`.

The shared configuration currently defines common formatting rules for Kotlin and Kotlin Gradle scripts, including:

```ini
max_line_length = 140
indent_style = space
indent_size = 4
```

## Shared resources

Some conventions package their default configuration as resources inside the Gradle plugin:

```text
src/
└── main/
    └── resources/
        ├── detekt/
        │   └── detekt.yml
        ├── editorconfig/
        │   └── .editorconfig
        └── git-hooks/
            └── pre-commit
```

This keeps the consuming repositories independent from the implementation details of the convention plugins.

## Project-specific configuration

The conventions provide defaults, but consuming projects can still provide repository-specific configuration where needed.

For example, Detekt can be overridden with:

```text
config/
└── detekt/
    └── detekt.yml
```

Other project-specific Gradle configuration can remain in the consuming repository instead of being added to this shared repository.

The goal is to centralize configuration that should be consistent across projects, not to eliminate project-specific configuration entirely.

## Development

Clone the repository and use the Gradle wrapper:

```bash
./gradlew build
```

To publish the conventions to the local Maven repository:

```bash
./gradlew publishToMavenLocal
```

The artifacts are then available from:

```text
~/.m2/repository/com/ingsisbry/
```

A consuming project can use the local publication by including `mavenLocal()` in its plugin/dependency resolution configuration when needed for local development.

## Publishing

The conventions are published under the Maven group:

```text
com.ingsisbry
```

The repository publishes to GitHub Packages:

```text
https://maven.pkg.github.com/Ingsis-BRY/gradle-conventions
```

Each convention is published as a Gradle plugin marker backed by the `gradle-conventions` artifact.

For example:

```text
com.ingsisbry.kotlin-module
com.ingsisbry.detekt
com.ingsisbry.git-hooks
```

all resolve to the corresponding convention implementation in this repository.

## Versioning

The project version can be provided through the `releaseVersion` Gradle property:

```bash
./gradlew publish -PreleaseVersion=1.0
```

The project uses the provided version without a leading `v`.

For example:

```bash
-PreleaseVersion=v1.0
```

is published as:

```text
1.0
```

## Repository structure

```text
gradle-conventions/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/
│   │   │       └── ingsisbry/
│   │   │           ├── coverage-aggregation.gradle.kts
│   │   │           ├── detekt.gradle.kts
│   │   │           ├── editor-config.gradle.kts
│   │   │           ├── git-hooks.gradle.kts
│   │   │           ├── kotlin-application.gradle.kts
│   │   │           └── kotlin-module.gradle.kts
│   │   └── resources/
│   │       ├── detekt/
│   │       │   └── detekt.yml
│   │       ├── editorconfig/
│   │       │   └── .editorconfig
│   │       └── git-hooks/
│   │           └── pre-commit
│   └── test/
│       └── ...
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```
