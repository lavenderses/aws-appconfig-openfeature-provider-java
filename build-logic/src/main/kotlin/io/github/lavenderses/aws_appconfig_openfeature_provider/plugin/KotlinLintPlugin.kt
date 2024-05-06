package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class KotlinLintPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(KtlintPlugin::class.java)
                apply(DetektPlugin::class.java)
            }

            with(extensions.getByType<KtlintExtension>()) {
                version.set(libs.findVersion("ktlint").get().requiredVersion)
                debug.set(true)
                verbose.set(true)
                android.set(false)
                outputToConsole.set(true)
                outputColorName.set("RED")
                ignoreFailures.set(false)
                reporters {
                    reporter(ReporterType.HTML)
                }
                filter {
                    exclude("**/generated/**")
                    include("**/kotlin/**")
                }
            }

            with(extensions.getByType<DetektExtension>()) {
                parallel = true
                config.setFrom(file("${rootProject.rootDir}/config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
            }

            val reportMerge = tasks.register<ReportMergeTask>("reportMerge") {
                output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
                input.from(tasks.withType<Detekt>().map { it.xmlReportFile })
            }

            with(tasks.withType<Detekt>()) {
                configureEach {
                    jvmTarget = target.PROJECT_JDK.javaVersion.toString()
                    reports {
                        html { require(true) }
                        xml { require(true) }
                        md { require(true) }
                    }
                    finalizedBy(reportMerge)
                    ignoreFailures = false
                }
            }

            with(tasks.withType<DetektCreateBaselineTask>()) {
                forEach { it.jvmTarget = target.PROJECT_JDK.string }
            }
        }
    }
}
