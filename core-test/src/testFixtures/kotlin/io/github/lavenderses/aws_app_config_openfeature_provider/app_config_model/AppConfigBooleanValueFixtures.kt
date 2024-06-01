package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

fun KClass<AppConfigBooleanValue>.fixture(
    enabled: Boolean = true,
    value: Boolean = true,
    @Language("json")
    jsonFormat: String = "{}",
) = AppConfigBooleanValue(
    /* enabled = */ enabled,
    /* value = */ value,
    /* jsonFormat = */ jsonFormat,
)
