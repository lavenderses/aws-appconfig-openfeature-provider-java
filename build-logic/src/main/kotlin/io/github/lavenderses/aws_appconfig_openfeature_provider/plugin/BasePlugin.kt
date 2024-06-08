package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JavaTestFixturesPlugin
import org.gradle.kotlin.dsl.getByType

class BasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // first party plugin
                apply(TestPlugin::class.java)

                // third party
                apply(JavaLibraryPlugin::class.java)
                apply(JavaTestFixturesPlugin::class.java)
            }

            group = projectGroupId
            version = projectVersion

            with(repositories) {
                mavenCentral()
            }

            with(extensions.getByType<JavaPluginExtension>()) {
                sourceCompatibility = PROJECT_JDK.javaVersion
                targetCompatibility = PROJECT_JDK.javaVersion
            }

            with(dependencies) {
                // third party
                compileOnly(libs.findLibrary("jetbrains-annotations").get())
                compileOnly(libs.findLibrary("log4j-slf4j2-impl").get())
                compileOnly(libs.findLibrary("slf4j-api").get())

                // bom
                implementation(platform(libs.findLibrary("aws-bom").get()))
            }
        }
    }
}
