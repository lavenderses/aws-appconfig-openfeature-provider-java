plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint.gradle.plugin)
    alias(libs.plugins.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register(libs.plugins.aws.appconfig.openfeature.provider.base.get().pluginId) {
            id = libs.plugins.aws.appconfig.openfeature.provider.base.get().pluginId
            implementationClass = "io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.BasePlugin"
        }
        register(libs.plugins.aws.appconfig.openfeature.provider.java.get().pluginId) {
            id = libs.plugins.aws.appconfig.openfeature.provider.java.get().pluginId
            implementationClass = "io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.JavaPlugin"
        }
        register(libs.plugins.aws.appconfig.openfeature.provider.kotlin.get().pluginId) {
            id = libs.plugins.aws.appconfig.openfeature.provider.kotlin.get().pluginId
            implementationClass = "io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.KotlinPlugin"
        }
        register(libs.plugins.aws.appconfig.openfeature.provider.lint.kotlin.get().pluginId) {
            id = libs.plugins.aws.appconfig.openfeature.provider.lint.kotlin.get().pluginId
            implementationClass = "io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.KotlinLintPlugin"
        }
        register(libs.plugins.aws.appconfig.openfeature.provider.test.get().pluginId) {
            id = libs.plugins.aws.appconfig.openfeature.provider.test.get().pluginId
            implementationClass = "io.github.lavenderses.aws_appconfig_openfeature_provider.plugin.TestPlugin"
        }
    }
}

dependencies {
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
    implementation(libs.lombok.gradle.plugin)
    implementation(libs.spotbugs.gradle.plugin)
}
