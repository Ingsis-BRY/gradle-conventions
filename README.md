# Gradle Conventions

Shared Gradle conventions for Kotlin and Spring Boot projects across multiple repositories.

## Why this repository exists

As the system grows into a multi-project, microservice-oriented architecture, duplicating build and development setup across repositories makes changes harder to maintain and conventions easier to diverge.

This repository centralizes common build practices and tooling into reusable Gradle convention plugins.

Projects can apply the same conventions without copying setup between repositories, while still keeping repository-specific configuration where it belongs.

The goal is to keep projects consistent and maintainable while allowing each repository to focus on its own domain and application logic.

## Provided conventions

| Convention | Plugin ID | Description |
| --- | --- | --- |
| Kotlin module | `com.ingsisbry.kotlin-module` | Common Kotlin/JVM configuration, JDK toolchain, and sources JAR generation |
| Kotlin quality | `com.ingsisbry.kotlin-quality` | Shared testing and quality tooling, including Detekt, Ktlint, JaCoCo, Git hooks, and EditorConfig |
| Spring Boot application | `com.ingsisbry.spring-boot-application` | Kotlin and Spring Boot application setup, including the Kotlin Spring plugin and Spring Web |
| JPA | `com.ingsisbry.jpa` | Kotlin JPA support, Kotlin reflection, and Spring Data JPA dependencies without imposing a database driver |
| Detekt | `com.ingsisbry.detekt` | Shared Detekt configuration with support for project-specific overrides |
| Coverage aggregation | `com.ingsisbry.coverage-aggregation` | Aggregated JaCoCo reports and project-wide coverage verification |
| Git hooks | `com.ingsisbry.git-hooks` | Installs a shared `pre-commit` hook that runs `./gradlew check` |
| Editor config | `com.ingsisbry.editor-config` | Installs the shared `.editorconfig` file when one does not already exist |

## Architecture

The conventions have distinct responsibilities. `kotlin-quality` builds on `kotlin-module`, while the Spring Boot and JPA conventions configure their respective requirements independently. Projects opt into each convention explicitly.

There is intentionally no separate `kotlin-application` convention. It was redundant for the repositories this project primarily serves: Spring Boot microservices use Spring Boot's `bootRun` task for local execution, while simple Kotlin/JVM applications can run their `main` entry point from an IDE or through their own execution setup without needing a shared Gradle `application` task. Removing this convention keeps the shared plugin set focused on configuration that consuming repositories actually need.

### Kotlin module

`com.ingsisbry.kotlin-module` provides the Kotlin/JVM foundation:

- Kotlin/JVM plugin
- JDK 21 toolchain
- Sources JAR generation

It does not configure testing or code quality tooling. Those responsibilities belong to `kotlin-quality`.

### Kotlin quality

`com.ingsisbry.kotlin-quality` builds on `kotlin-module` and configures the shared development and quality tooling:

- Detekt
- Ktlint
- JaCoCo
- JUnit Platform
- Kotlin test dependencies
- Git hook installation
- EditorConfig installation

This convention ensures that Kotlin modules using the quality convention share the same baseline without duplicating configuration.

### Spring Boot application

`com.ingsisbry.spring-boot-application` configures the Kotlin and Spring Boot plugins directly.

It applies:

- Kotlin/JVM
- Spring Boot
- Spring dependency management
- The Kotlin Spring compiler plugin
- Spring Web

It also adds the Spring Boot Web starter dependency.

This convention is intentionally independent of `kotlin-quality`. Projects apply `kotlin-quality` explicitly when they want the shared testing and quality baseline. Keeping framework setup separate from quality tooling allows each convention to remain focused while making both part of the recommended standard for microservices.

The Kotlin Spring compiler plugin provides Spring-aware compiler behavior, including support for Spring's proxy-based features and annotations such as `@Transactional`.

### JPA

`com.ingsisbry.jpa` configures Kotlin's JPA compiler plugin and adds the dependencies required for Kotlin reflection and Spring Data JPA.

It is intended to complement a Spring Boot project that already provides compatible dependency management.

**The JPA convention does not impose a specific relational database or database driver.** Each consuming repository must explicitly declare the driver for its preferred database, such as PostgreSQL, MySQL, or MariaDB.

This keeps the shared convention focused on persistence support while leaving database selection to the application.

## Usage

Projects consume the published conventions through their Gradle `plugins` block.

### Kotlin module

```kotlin
plugins {
    id("com.ingsisbry.kotlin-module") version "2.0.0"
}
```

This configures Kotlin/JVM, the JDK 21 toolchain, and sources JAR generation.

### Kotlin quality

```kotlin
plugins {
    id("com.ingsisbry.kotlin-quality") version "2.0.0"
}
```

This applies the Kotlin module convention and configures the shared testing and quality tooling. There is no need to apply `kotlin-module` separately unless a project has a specific reason to configure it independently.

### Spring Boot application

```kotlin
plugins {
    id("com.ingsisbry.spring-boot-application") version "2.0.0"
}
```

This configures Kotlin/JVM and the Spring Boot application requirements. It does not automatically apply `kotlin-quality`; apply that convention separately when the project needs the shared quality baseline.

### JPA

```kotlin
plugins {
    id("com.ingsisbry.jpa") version "2.0.0"
}
```

Apply this convention to a project that needs Kotlin JPA support and Spring Data JPA. The consuming project should also have compatible Spring Boot dependency management configured.

The database driver must be declared separately in the consuming repository. For example, a project using PostgreSQL can declare:

```kotlin
dependencies {
    runtimeOnly("org.postgresql:postgresql")
}
```

This keeps the database choice explicit and prevents the shared JPA convention from imposing a particular database on every consuming project.

### Combining conventions

Multiple conventions can be applied to the same project when their responsibilities are complementary.

For a typical Kotlin Spring Boot microservice that uses JPA, **the recommended standard is to apply all three conventions**: `kotlin-quality`, `spring-boot-application`, and `jpa`.

```kotlin
plugins {
    id("com.ingsisbry.kotlin-quality") version "2.0.0"
    id("com.ingsisbry.spring-boot-application") version "2.0.0"
    id("com.ingsisbry.jpa") version "2.0.0"
}
```

Its dependencies could then include the PostgreSQL driver:

```kotlin
dependencies {
    runtimeOnly("org.postgresql:postgresql")
}
```

The conventions have distinct responsibilities:

- **Kotlin quality** provides the shared testing and quality standards and applies the Kotlin module convention.
- **Spring Boot application** configures Kotlin and the Spring Boot framework, including Spring Web.
- **JPA** adds Kotlin JPA support and Spring Data JPA dependencies without imposing a specific database driver.

This combination provides a consistent baseline for typical Spring Boot services while allowing each repository to choose its database and retain its application-specific configuration. For services that do not use JPA, omit the JPA convention. For Kotlin/JVM modules that do not use Spring Boot, `kotlin-quality` can be used on its own.

## Conventions

### Kotlin module

Plugin ID: `com.ingsisbry.kotlin-module`

Provides the common configuration for Kotlin/JVM modules:

- Kotlin/JVM
- JDK 21 toolchain
- Sources JAR generation

It intentionally does not configure quality tooling. This allows the Kotlin/JVM foundation to remain focused on language and compilation requirements.

### Kotlin quality

Plugin ID: `com.ingsisbry.kotlin-quality`

Builds on `com.ingsisbry.kotlin-module`.

Provides:

- Detekt
- Ktlint
- JaCoCo
- JUnit Platform
- Kotlin test dependencies
- Git hook installation task
- EditorConfig installation task

JaCoCo is configured to use version `0.8.15`.

The convention uses the shared Detekt configuration by default and supports repository-specific overrides.

The quality convention is Kotlin/JVM-specific because it applies the Kotlin module convention. This is intentional: it centralizes the quality baseline needed by the current Kotlin projects without introducing a separate convention for every tool.

### Spring Boot application

Plugin ID: `com.ingsisbry.spring-boot-application`

Configures the plugins and dependencies needed by a Kotlin Spring Boot web application.

It applies:

- Kotlin/JVM
- Spring Boot
- Spring dependency management
- Kotlin Spring compiler plugin

It also adds the Spring Boot Web starter.

The convention intentionally does not apply `kotlin-quality`. Projects opt into the shared quality baseline explicitly by applying `com.ingsisbry.kotlin-quality` alongside this convention.

### JPA

Plugin ID: `com.ingsisbry.jpa`

Provides:

- Kotlin JPA compiler plugin
- Kotlin reflection
- Spring Data JPA starter

The convention is designed for projects with compatible Spring Boot dependency management.

It **does not declare a database driver**. Consuming repositories must explicitly select and add the driver for their chosen relational database.

For example, a PostgreSQL-based project can use:

```kotlin
dependencies {
    runtimeOnly("org.postgresql:postgresql")
}
```

Another project can choose a different supported database without needing to modify or fork the shared convention. This keeps database-specific decisions in the application repository, where they belong.

### Detekt

Plugin ID: `com.ingsisbry.detekt`

Provides a shared default Detekt configuration.

The default configuration is packaged inside the convention plugin, so consuming projects do not need to copy `detekt.yml` into their repositories.

A project can override the default configuration by providing:

```text
config/
└── detekt/
    └── detekt.yml
```

When this file exists, it is used instead of the shared configuration. The shared convention fails the build when Detekt reports findings.

### Coverage aggregation

Plugin ID: `com.ingsisbry.coverage-aggregation`

Aggregates JaCoCo coverage across the subprojects of the consuming repository.

It provides:

```text
coverageReport
coverageVerification
```

`coverageReport` generates HTML and XML coverage reports. `coverageVerification` verifies the aggregated coverage against the configured minimum thresholds.

The current thresholds require at least 80% coverage for:

- Instructions
- Branches
- Lines
- Complexity
- Methods
- Classes

The verification task is also attached to the root `check` task.

**Coverage aggregation belongs at the root project level**, where it can aggregate results from all relevant subprojects. It should not be applied indiscriminately to every subproject.

### Git hooks

Plugin ID: `com.ingsisbry.git-hooks`

Provides the task:

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

An existing `pre-commit` hook is not overwritten. This allows the convention to provide a default hook without requiring every consuming repository to maintain its own copy. The task installs the hook explicitly rather than modifying Git hooks automatically during every build.

### Editor config

Plugin ID: `com.ingsisbry.editor-config`

Provides the task:

```text
installEditorConfig
```

Running:

```bash
./gradlew installEditorConfig
```

installs the shared `.editorconfig` at the root of the consuming repository. The convention does not overwrite an existing `.editorconfig`.

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

This keeps the consuming repositories independent from the implementation details of the convention plugins. The resources are embedded in the published plugin artifact and extracted or installed when the corresponding tasks run.

## Project-specific configuration

The conventions provide defaults, but consuming projects can still provide repository-specific configuration where needed.

For example, Detekt can be overridden with:

```text
config/
└── detekt/
    └── detekt.yml
```

Database drivers are declared by consuming projects rather than by the shared JPA convention. Other project-specific Gradle configuration can remain in the consuming repository instead of being added to this shared repository.

The goal is to centralize practices that should be consistent across projects, not to eliminate project-specific configuration entirely.

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

A consuming project can use the local publication by including `mavenLocal()` in its plugin or dependency resolution configuration when needed for local development.

## Publishing

The conventions are published under the Maven group:

```text
com.ingsisbry
```

The repository publishes to GitHub Packages:

[GitHub Packages](https://maven.pkg.github.com/Ingsis-BRY/gradle-conventions)

Each convention is published as a Gradle plugin marker backed by the `gradle-conventions` artifact.

The published plugin IDs are:

```text
com.ingsisbry.kotlin-module
com.ingsisbry.kotlin-quality
com.ingsisbry.spring-boot-application
com.ingsisbry.jpa
com.ingsisbry.detekt
com.ingsisbry.git-hooks
com.ingsisbry.editor-config
com.ingsisbry.coverage-aggregation
```

These plugin IDs resolve to their corresponding convention implementations in this repository.

## Versioning

The project version is supplied through the `releaseVersion` Gradle property during publication:

```bash
./gradlew publish -PreleaseVersion=2.0.0
```

A leading `v` is removed from the supplied version. For example:

```bash
./gradlew publish -PreleaseVersion=v2.0.0
```

is published as:

```text
2.0.0
```

The release workflow can supply this property from the GitHub Release tag, keeping the release tag as the source of truth for the published version.

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
│   │   │           ├── jpa.gradle.kts
│   │   │           ├── kotlin-module.gradle.kts
│   │   │           ├── kotlin-quality.gradle.kts
│   │   │           └── spring-boot-application.gradle.kts
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