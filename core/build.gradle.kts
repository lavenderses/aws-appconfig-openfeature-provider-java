plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.aws.appconfig.openfeature.provider.java)
    alias(libs.plugins.aws.appconfig.openfeature.provider.lint.kotlin)
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.lavenderses"
            artifactId = "aws-appconfig-openfeature-provider-java"
            version = "0.1.0"

            from(components["java"])

            pom {
                name.set("aws-appconfig-openfeature-provider-java")
                description.set("OpenFeature Provider third-party implementation for AWS AppConfig in Java")
                url.set("https://github.com/lavenderses/AWSAppConfig-OpenFeature-provider-java")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}
