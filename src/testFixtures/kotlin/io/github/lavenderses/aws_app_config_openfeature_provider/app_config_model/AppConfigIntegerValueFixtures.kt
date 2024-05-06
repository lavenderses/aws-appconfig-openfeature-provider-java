package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

fun KClass<AppConfigIntegerValue>.fixture(
    enabled: Boolean = true,
    value: Int = 12345,
    @Language("json")
    jsonFormat: String = "{}",
) = AppConfigIntegerValue(
    /* enabled = */ enabled,
    /* value = */ value,
    /* jsonFormat = */ jsonFormat,
)
