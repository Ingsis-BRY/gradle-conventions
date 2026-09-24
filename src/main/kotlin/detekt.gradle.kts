plugins {
    id("dev.detekt")
}

val defaultDetektConfig = layout.buildDirectory.file(
    "detekt/default-detekt.yml"
)

val extractDefaultDetektConfig = tasks.register(
    "extractDefaultDetektConfig"
) {
    outputs.file(defaultDetektConfig)

    doLast {
        val resource = javaClass.classLoader.getResourceAsStream(
            "detekt/detekt.yml"
        ) ?: error(
            "Could not find default Detekt configuration: " +
                    "detekt/detekt.yml"
        )

        resource.use { input ->
            defaultDetektConfig.get().asFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    val customDetektConfig = rootProject.file(
        "config/detekt/detekt.yml"
    )

    dependsOn(extractDefaultDetektConfig)

    config.setFrom(
        if (customDetektConfig.exists()) {
            customDetektConfig
        } else {
            defaultDetektConfig
        }
    )

    buildUponDefaultConfig.set(false)
    ignoreFailures.set(false)
}