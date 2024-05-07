package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

class TestPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            tasks.withType<Test>() {
                useJUnitPlatform()
            }

            with(dependencies) {
                // bom
                testImplementation(platform(libs.findLibrary("mockito-bom").get()))

                // third party
                testImplementation(libs.findLibrary("assertk").get())
                testImplementation(libs.findLibrary("junit-jupiter-engine").get())
                testImplementation(libs.findLibrary("kotlin-test").get())
                testImplementation(libs.findLibrary("kotlin-test-junit5").get())
                testImplementation(libs.findLibrary("kotlin-reflect").get())
                testImplementation(libs.findLibrary("mockito-junit-jupiter").get())
                testImplementation(libs.findLibrary("mockito-inline").get())
                testImplementation(libs.findLibrary("mockito-kotlin").get())
            }
        }
    }
}
