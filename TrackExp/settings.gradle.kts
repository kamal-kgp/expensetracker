// settings.gradle.kts (Project Root)

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
        // You might need jcenter() for older libraries, but it's generally deprecated
        // jcenter()
    }
}

rootProject.name = "ExpenseTracker"
include(":app")

