plugins {
    id("kotlin-module")
    application
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}