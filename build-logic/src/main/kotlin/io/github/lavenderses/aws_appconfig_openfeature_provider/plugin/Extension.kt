@file:Suppress("MatchingDeclarationName")

package io.github.lavenderses.aws_appconfig_openfeature_provider.plugin

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

data class JdkVersion(
    val string: String,
    val number: Int,
    val javaVersion: JavaVersion,
    val jvmTarget: JvmTarget,
)

private val VERSION: JavaVersion = JavaVersion.VERSION_17

val Project.PROJECT_JDK: JdkVersion
    get() = JdkVersion(
        string = VERSION.majorVersion,
        number = VERSION.majorVersion.toInt(),
        javaVersion = VERSION,
        jvmTarget = JvmTarget.JVM_17,
    )

val Project.libs: VersionCatalog get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.publicationName: String get() = "maven"

val Project.projectGroupId: String get() = "io.github.lavenderses"

val Project.projectVersion: String get() = "0.5.1"

fun DependencyHandler.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

fun DependencyHandler.api(dependencyNotation: Any) {
    add("api", dependencyNotation)
}

fun DependencyHandler.compileOnly(dependencyNotation: Any) {
    add("compileOnly", dependencyNotation)
}

fun DependencyHandler.runtimeOnly(dependencyNotation: Any) {
    add("runtimeOnly", dependencyNotation)
}

fun DependencyHandler.testImplementation(dependencyNotation: Any) {
    add("testImplementation", dependencyNotation)
}

fun DependencyHandler.testFixturesImplementation(dependencyNotation: Any) {
    add("testFixturesImplementation", dependencyNotation)
}
