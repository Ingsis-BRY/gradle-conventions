package com.ingsisbry

val pluginClassLoader = this::class.java.classLoader
val gitHook = rootProject.file(".git/hooks/pre-commit")

tasks.register("installGitHook") {
    doLast {
        if (gitHook.exists()) {
            println("Git hook already exists: ${gitHook.absolutePath}")
            return@doLast
        }

        val resource = pluginClassLoader.getResourceAsStream(
            "git-hooks/pre-commit"
        ) ?: error(
            "Could not find default pre-commit hook: " +
                    "git-hooks/pre-commit"
        )

        gitHook.parentFile.mkdirs()

        resource.use { input ->
            gitHook.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        gitHook.setExecutable(true)
    }
}