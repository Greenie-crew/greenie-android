pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "noise-doctor"
include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:data")
include(":core:domain")
include(":core:network")
include(":core:model")
include(":core:ui")
include(":feature:record")
include(":feature:history")
include(":feature:menu")
include(":core:service")
include(":feature:result")
include(":core:database")
