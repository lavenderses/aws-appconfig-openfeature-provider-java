plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.aws.appconfig.openfeature.provider.kotlin)
    alias(libs.plugins.aws.appconfig.openfeature.provider.lint.kotlin)
}

description = "Separated module for testing :core module"

dependencies {
    testImplementation(project(":core"))
    testFixturesImplementation(project(":core"))

    testImplementation(libs.openfeature)
    testImplementation(libs.aws.appconfigdata)
    testImplementation(libs.jackson.databind)

    testFixturesImplementation(libs.openfeature)
    testFixturesImplementation(libs.jackson.databind)
}
