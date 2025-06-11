pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
    }
}

plugins {
    id("de.fayard.refreshVersions")
}


rootProject.name = "stream-fix"

include(
    "stream-fix-apps:app-api",
    "stream-fix-apps:app-batch",
    "stream-fix-adapters:adapter-http",
    "stream-fix-adapters:adapter-persistence",
    "stream-fix-adapters:adapter-redis",
    "stream-fix-commons",
    "stream-fix-core:core-domain",
    "stream-fix-core:core-port",
    "stream-fix-core:core-service",
    "stream-fix-core:core-usecase"
)
