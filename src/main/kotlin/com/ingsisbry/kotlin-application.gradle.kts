package com.ingsisbry

plugins {
    id("com.ingsisbry.kotlin-module")
    application
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}