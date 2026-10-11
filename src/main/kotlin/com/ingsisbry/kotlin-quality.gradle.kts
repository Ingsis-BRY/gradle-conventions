package com.ingsisbry

plugins {
    id("com.ingsisbry.kotlin-module")
    id("com.ingsisbry.git-hooks")
    id("com.ingsisbry.editor-config")
    id("dev.detekt")
    id("org.jlleitschuh.gradle.ktlint")
    jacoco
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.15"
}