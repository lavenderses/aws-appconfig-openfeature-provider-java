import io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.projectGroupId
import io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.projectVersion

plugins {
    java
    `maven-publish`
    alias(libs.plugins.aws.appconfig.openfeature.provider.java)
    alias(libs.plugins.aws.appconfig.openfeature.provider.publication)
}

description = "OpenFeature Provider third-party implementation for AWS AppConfig in Java"

dependencies {
    compileOnly(libs.openfeature)
    implementation(libs.aws.appconfigdata)
    implementation(libs.jackson.databind)
    implementation(libs.jakarta.annotation.api)
}

mavenPublishing {
    coordinates(projectGroupId, "aws-appconfig-openfeature-provider-java", projectVersion)

    pom {
        name.set("OpenFeature Provider implementation for AWS AppConfig in Java")
        description.set("OpenFeature Provider third-party implementation for AWS AppConfig in Java")
    }
}
