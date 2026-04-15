pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "lab5"
include(":app")
include(":core")
include(":core:navigation")
include(":feature:catalog:api")
include(":feature:catalog:domain")
include(":feature:catalog:data")
include(":feature:catalog:ui")
include(":feature:favorites:api")
include(":feature:favorites:domain")
include(":feature:favorites:data")
include(":feature:favorites:ui")
 
