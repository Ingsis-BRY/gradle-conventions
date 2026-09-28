plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.ingsisbry"
version = (
        findProperty("releaseVersion") as String?
            ?: "1.0"
        ).removePrefix("v")

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.10")
    implementation("dev.detekt:dev.detekt.gradle.plugin:2.0.0-alpha.3")
    implementation(
        "org.jlleitschuh.gradle.ktlint:" +
                "org.jlleitschuh.gradle.ktlint.gradle.plugin:14.2.0"
    )
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri(
                "https://maven.pkg.github.com/Ingsis-BRY/gradle-conventions"
            )

            credentials {
                username =
                    project.findProperty("gpr.user") as String?
                        ?: System.getenv("USERNAME")

                password =
                    project.findProperty("gpr.key") as String?
                        ?: System.getenv("TOKEN")
            }
        }
    }
}