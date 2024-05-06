package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

fun KClass<AppConfigDoubleValue>.fixture(
    enabled: Boolean = true,
    value: Double = 12345.0,
    @Language("json")
    jsonFormat: String = "{}",
) = AppConfigDoubleValue(
    /* enabled = */ enabled,
    /* value = */ value,
    /* jsonFormat = */ jsonFormat,
)
