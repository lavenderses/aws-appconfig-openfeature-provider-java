package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JavaTestFixturesPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class BasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            group = "io.github.lavenderses"

            with(pluginManager) {
                // first party plugin
                apply(TestPlugin::class.java)

                // third party
                apply(JavaLibraryPlugin::class.java)
                apply(JavaTestFixturesPlugin::class.java)
                apply(libs.findPlugin("kotlin-jvm").get().get().pluginId)
            }

            with(repositories) {
                mavenCentral()
            }

            with(extensions.getByType<JavaPluginExtension>()) {
                sourceCompatibility = PROJECT_JDK.javaVersion
                targetCompatibility = PROJECT_JDK.javaVersion
            }

            with(extensions.getByType<KotlinJvmProjectExtension>()) {
                compilerOptions {
                    this.jvmTarget.set(PROJECT_JDK.jvmTarget)
                }
            }

            with(dependencies) {
                // third party
                api(libs.findLibrary("jetbrains-annotations").get())
                api(libs.findLibrary("log4j-slf4j2-impl").get())
                api(libs.findLibrary("slf4j-api").get())

                // bom
                implementation(platform(libs.findLibrary("aws-bom").get()))
            }

            tasks.withType<Jar> {
                group = "io.github.lavenderses"
                archiveBaseName.set("aws-app-config-openfeature-provider-java")
            }
        }
    }
}
