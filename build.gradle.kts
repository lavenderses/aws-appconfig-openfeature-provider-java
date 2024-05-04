import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("io.freefair.lombok") version "8.6"
    // TODO
    // id("com.github.spotbugs") version "6.0.13"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

repositories {
    mavenCentral()
}

group = "io.github.lavenderses"
description = "OpenFeature Provider third-party implementation for AWS AppConfig in Java"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.25.44"))

    compileOnly("dev.openfeature:sdk:1.7.6")
    implementation("software.amazon.awssdk:appconfigdata")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    // spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")
    // spotbugs("com.github.spotbugs:spotbugs:4.8.0")

    testImplementation(platform("org.mockito:mockito-bom:5.11.0"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")

    testImplementation("dev.openfeature:sdk:1.7.6")

    testFixturesImplementation("dev.openfeature:sdk:1.7.6")
}

tasks.withType<Jar> {
    group = "io.github.lavenderses"
    archiveBaseName.set("aws-app-config-openfeature-provider-java")
}

tasks.withType<Test> {
    useJUnitPlatform()
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

val reportMerge by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
}

subprojects {
    tasks.withType<Detekt>().configureEach {
        reports.xml.required.set(true)
        finalizedBy(reportMerge)
        ignoreFailures = false
    }

    reportMerge.configure {
        input.from(tasks.withType<Detekt>().map { it.xmlReportFile })
    }
}

detekt {
    parallel = true
    config.setFrom(files("${rootProject.rootDir}/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    basePath = rootProject.projectDir.absolutePath
}

ktlint {
    ignoreFailures.set(false)
}
