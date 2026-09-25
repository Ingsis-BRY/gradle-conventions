# Gradle Conventions
Shared Gradle conventions for the projects that make up the architecture.

## Why this repository exists

As the system grows into a multi-project, microservice-oriented architecture, duplicating build and development configuration across repositories makes changes harder to maintain and conventions easier to diverge.

This repository centralizes common configuration such as Kotlin, linters, formatters, static analysis, test coverage and Git hooks, allowing projects to consume the same conventions as reusable Gradle plugins instead of copying configuration between repositories.

The goal is to keep projects consistent while allowing each repository to focus on its own domain and application logic.

## Provided conventions

* **Kotlin module** — common Kotlin/JVM, testing, Ktlint, Detekt and JaCoCo configuration.
* **Kotlin application** — application projects built on top of the Kotlin module convention.
* **Detekt** — shared static analysis configuration with a default `detekt.yml` and support for project-specific overrides.
* **Coverage aggregation** — aggregated JaCoCo reports and project-wide coverage verification.
* **Git hooks** — shared `pre-commit` hook that runs `./gradlew check`.

## Usage

Projects consume the published conventions through their Gradle `plugins` block:

```kotlin
plugins {
    id("com.ingsisbry.kotlin-module") version "1.0.0"
}
```

A project can also override specific configuration when necessary. For example, Detekt uses the shared default configuration unless the consuming repository provides:

```text
config/
└── detekt/
    └── detekt.yml
```
