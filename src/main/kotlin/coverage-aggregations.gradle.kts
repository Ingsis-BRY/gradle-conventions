plugins {
    base
    jacoco
}

repositories {
    mavenCentral()
}

val coveredProjects = subprojects

val executionFiles = files(
    coveredProjects.map {
        it.layout.buildDirectory.file("jacoco/test.exec")
    }
)

val sourceDirs = files(
    coveredProjects.map {
        it.file("src/main/kotlin")
    }
)

val classDirs = files(
    coveredProjects.map {
        it.layout.buildDirectory.dir("classes/kotlin/main")
    }
).asFileTree

/**
 * Aggregates JaCoCo coverage from all subprojects into a single report.
 *
 * The resulting report can be consumed both by developers through HTML
 * and by CI through XML.
 */
val coverageReport by tasks.registering(JacocoReport::class) {
    dependsOn(
        coveredProjects.map {
            it.tasks.named("test")
        }
    )

    executionData.setFrom(
        executionFiles.filter { it.exists() }
    )

    sourceDirectories.setFrom(sourceDirs)
    classDirectories.setFrom(classDirs)

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

/**
 * Quality gate for the whole project.
 *
 * The threshold is applied to the aggregated project rather than to each
 * individual subproject, preventing a small module from failing the build
 * because of its own local coverage percentage.
 */
val coverageVerification by tasks.registering(
    JacocoCoverageVerification::class
) {
    dependsOn(coverageReport)

    executionData.setFrom(
        executionFiles.filter { it.exists() }
    )

    sourceDirectories.setFrom(sourceDirs)
    classDirectories.setFrom(classDirs)

    violationRules {
        rule {
            element = "BUNDLE"

            listOf(
                "INSTRUCTION",
                "BRANCH",
                "LINE",
                "COMPLEXITY",
                "METHOD",
                "CLASS",
            ).forEach { metric ->
                limit {
                    counter = metric
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(coverageVerification)
}