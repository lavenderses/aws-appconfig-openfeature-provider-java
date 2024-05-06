package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class JavaPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // first party
                apply(BasePlugin::class.java)

                // third party
                apply(LombokPlugin::class.java)
                // TODO
                // apply(SpotBugsPlugin::class.java)
            }
        }
    }
}
