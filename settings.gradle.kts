pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Repositório oficial do Shizuku-API
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "RecoveryLab"
include(":app")
