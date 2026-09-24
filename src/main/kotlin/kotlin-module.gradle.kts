plugins {
    kotlin("jvm")
    id("detekt")
    id("org.jlleitschuh.gradle.ktlint")
    jacoco
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
}

jacoco {
    toolVersion = "0.8.15"
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}