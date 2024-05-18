package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class PublicationPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(MavenPublishPlugin::class.java)
                apply(com.vanniktech.maven.publish.MavenPublishPlugin::class.java)
            }

            with(extensions.getByType<MavenPublishBaseExtension>()) {
                coordinates(projectGroupId, "aws-appconfig-openfeature-provider-java", projectVersion)

                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
                signAllPublications()

                pom {
                    inceptionYear.set("2024")
                    url.set("https://github.com/lavenderses/AWSAppConfig-OpenFeature-provider-java")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://github.com/lavenderses/AWSAppConfig-OpenFeature-provider-java/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("lavenderses")
                            name.set("cat")
                            email.set(System.getenv("EMAIL"))
                        }
                    }

                    scm {
                        url.set("https://github.com/lavenderses/AWSAppConfig-OpenFeature-provider-java")
                    }
                }
            }

            tasks.withType<GenerateModuleMetadata> {
                // TODO: Remove kotlinSourcesJar
                dependsOn("kotlinSourcesJar", "plainJavadocJar")
            }
        }
    }
}
