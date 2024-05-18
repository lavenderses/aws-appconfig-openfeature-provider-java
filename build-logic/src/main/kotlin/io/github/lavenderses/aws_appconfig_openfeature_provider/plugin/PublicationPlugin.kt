package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import cl.franciscosolis.sonatypecentralupload.SonatypeCentralUploadPlugin
import cl.franciscosolis.sonatypecentralupload.SonatypeCentralUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class PublicationPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(MavenPublishPlugin::class.java)
                apply(SonatypeCentralUploadPlugin::class.java)
            }

            with(extensions.getByType<JavaPluginExtension>()) {
                withSourcesJar()
                withJavadocJar()
            }

            with(extensions.getByType<PublishingExtension>()) {
                publications {
                    register<MavenPublication>(publicationName) {
                        groupId = "io.github.lavenderses"

                        from(components["java"])

                        pom {
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
                }
            }

            tasks.withType<SonatypeCentralUploadTask> {
                dependsOn("jar", "sourcesJar", "javadocJar", "generatePomFileForMavenPublication")

                username.set(System.getenv("SONATYPE_USERNAME"))
                password.set(System.getenv("SONATYPE_PASSWORD"))

                archives.set(
                    files(
                        tasks.named("jar"),
                        tasks.named("sourcesJar"),
                        tasks.named("javadocJar"),
                    )
                )

                pom.set(
                    file(tasks.named("generatePomFileForMavenPublication").get().outputs.files.single())
                )

                signingKey.set(System.getenv("PGP_SIGNING_KEY"))
                signingKeyPassphrase.set(System.getenv("PGP_SIGNING_KEY_PASSPHRASE"))
            }
        }
    }
}