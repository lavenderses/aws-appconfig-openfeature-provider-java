package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class KotlinPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // first party plugin
                apply(BasePlugin::class.java)
                apply(KotlinLintPlugin::class.java)
            }

            with(dependencies) {
                implementation(libs.findLibrary("jackson-module-kotlin").get())
            }
        }
    }
}
