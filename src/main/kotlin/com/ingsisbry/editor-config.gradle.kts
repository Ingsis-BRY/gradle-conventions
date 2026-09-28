package com.ingsisbry

val pluginClassLoader = this::class.java.classLoader
val editorConfig = rootProject.file(".editorconfig")

tasks.register("installEditorConfig") {
    doLast {
        if (editorConfig.exists()) {
            return@doLast
        }

        val resource = pluginClassLoader.getResourceAsStream(
            "editorconfig/.editorconfig"
        ) ?: error(
            "Could not find default .editorconfig: " +
                    "editorconfig/.editorconfig"
        )

        resource.use { input ->
            editorConfig.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}