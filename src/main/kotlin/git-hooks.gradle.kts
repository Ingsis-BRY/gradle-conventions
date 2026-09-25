val preCommitSource = rootProject.file("scripts/pre-commit")
val gitHook = rootProject.file(".git/hooks/pre-commit")

tasks.register("installGitHook") {
    doLast {
        val resource = javaClass.classLoader.getResourceAsStream(
            "git-hooks/pre-commit"
        ) ?: error(
            "Could not find default pre-commit hook: " +
                    "git-hooks/pre-commit"
        )

        preCommitSource.parentFile.mkdirs()

        resource.use { input ->
            preCommitSource.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        gitHook.parentFile.mkdirs()
        preCommitSource.copyTo(gitHook, overwrite = true)
        gitHook.setExecutable(true)
    }
}