import io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.projectGroupId
import io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.projectVersion

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.aws.appconfig.openfeature.provider.java)
    alias(libs.plugins.aws.appconfig.openfeature.provider.lint.kotlin)
    alias(libs.plugins.aws.appconfig.openfeature.provider.publication)
}

description = "OpenFeature Provider third-party implementation for AWS AppConfig in Java"

dependencies {
    compileOnly(libs.openfeature)
    implementation(libs.aws.appconfigdata)
    implementation(libs.jackson.databind)

    // spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")
    // spotbugs("com.github.spotbugs:spotbugs:4.8.0")

    testImplementation(libs.openfeature)
    testFixturesImplementation(libs.openfeature)
    testFixturesImplementation(libs.jackson.databind)
}

mavenPublishing {
    coordinates(projectGroupId, "aws-appconfig-openfeature-provider-java", projectVersion)

    pom {
        name.set("OpenFeature Provider implementation for AWS AppConfig in Java")
        description.set("OpenFeature Provider third-party implementation for AWS AppConfig in Java")
    }
}
