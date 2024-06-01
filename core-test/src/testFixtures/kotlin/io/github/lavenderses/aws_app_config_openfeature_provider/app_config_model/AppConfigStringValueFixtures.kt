package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

fun KClass<AppConfigStringValue>.fixture(
    enabled: Boolean = true,
    value: String = "text",
    @Language("json")
    jsonFormat: String = "{}",
) = AppConfigStringValue(
    /* enabled = */ enabled,
    /* value = */ value,
    /* jsonFormat = */ jsonFormat,
)
