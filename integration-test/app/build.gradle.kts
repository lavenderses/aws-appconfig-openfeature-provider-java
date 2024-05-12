plugins {
    application
    alias(libs.plugins.jib.plugin)
    alias(libs.plugins.kotlin.jvm)

    // first party
    alias(libs.plugins.aws.appconfig.openfeature.provider.kotlin)
}

description = "Test application for integration testing OpenFeature Provider implementation"

dependencies {
    // first party
    implementation(project(":core"))
    implementation(project(":integration-test:model"))

    // third party
    implementation(platform(libs.armeria.bom))
    implementation(libs.armeria)
    implementation(libs.armeria.kotlin)
    implementation(libs.jackson.databind)
    implementation(libs.openfeature)
}

application {
    mainClass = "io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.app.IntegrationTestApplicationKt"
}

jib {
    to {
        image = "awsappconfig-openfeature-provider-java"
        tags = setOf("integration-test")
    }
}
