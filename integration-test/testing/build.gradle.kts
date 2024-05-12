plugins {
    alias(libs.plugins.kotlin.jvm)

    // first party
    alias(libs.plugins.aws.appconfig.openfeature.provider.kotlin)
}

description = "Integration test for OpenFeature Provider third-party implementation for AWS AppConfig in Java"

dependencies {
    testImplementation(project(":integration-test:model"))

    // third party
    testImplementation(platform(libs.armeria.bom))
    testImplementation(libs.armeria)
    testImplementation(libs.armeria.kotlin)
    testImplementation(libs.jsonasesrt)
}
