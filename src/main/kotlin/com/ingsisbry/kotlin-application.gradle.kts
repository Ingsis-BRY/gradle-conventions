package com.ingsisbry

plugins {
    id("com.ingsisbry.kotlin-quality")
    application
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}