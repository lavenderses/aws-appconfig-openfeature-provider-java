rootProject.name = "AWSAppConfig-openfeature-provider-java"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

include(":core")
include(":core-test")
include(":integration-test")
include(":integration-test:app")
include(":integration-test:model")
include(":integration-test:testing")

includeBuild("build-logic")
