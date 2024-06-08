package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class KotlinPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // first party plugin
                apply(BasePlugin::class.java)
                apply(KotlinLintPlugin::class.java)
                apply(libs.findPlugin("kotlin-jvm").get().get().pluginId)
            }

            with(extensions.getByType<KotlinJvmProjectExtension>()) {
                compilerOptions {
                    this.jvmTarget.set(PROJECT_JDK.jvmTarget)
                }
            }

            with(dependencies) {
                implementation(libs.findLibrary("jackson-module-kotlin").get())
                implementation(libs.findLibrary("kotlin-reflect").get())
                implementation(libs.findLibrary("kotlin-logging").get())
                implementation(libs.findLibrary("kotlin-logging-jvm").get())
                // Kotlin plugin is for subproject, not exported.
                runtimeOnly(libs.findLibrary("log4j-slf4j2-impl").get())
                runtimeOnly(libs.findLibrary("slf4j-api").get())
            }
        }
    }
}
