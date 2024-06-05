package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsPlugin
import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class JavaPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // first party
                apply(BasePlugin::class.java)

                // third party
                apply(LombokPlugin::class.java)
                apply(SpotBugsPlugin::class.java)
                apply(SpotlessPlugin::class.java)
            }

            with(dependencies) {
                add("spotbugsPlugins", libs.findLibrary("spotbugs-plugin").get())
                add("spotbugs", libs.findLibrary("spotbugs").get())
            }

            with(extensions.getByType<SpotlessExtension>()) {
                java {
                    indentWithSpaces(4)
                    googleJavaFormat()
                        .aosp()
                        .skipJavadocFormatting()
                    trimTrailingWhitespace()
                    endWithNewline()
                    removeUnusedImports()
                }
            }

            with(extensions.getByType<SpotBugsExtension>()) {
                excludeFilter.set(
                    file("${rootProject.rootDir}/config/spotbugs/exclude.xml")
                )
            }
        }
    }
}
