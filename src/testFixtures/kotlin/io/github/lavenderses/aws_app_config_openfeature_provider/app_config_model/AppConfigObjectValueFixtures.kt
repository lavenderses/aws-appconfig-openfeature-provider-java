package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import dev.openfeature.sdk.ImmutableStructure
import dev.openfeature.sdk.Value
import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass

fun KClass<AppConfigObjectValue>.fixture(
    enabled: Boolean = true,
    value: Value = Value(ImmutableStructure(emptyMap())),
    @Language("json")
    jsonFormat: String = "{}",
) = AppConfigObjectValue(
    /* enabled = */ enabled,
    /* value = */ value,
    /* jsonFormat = */ jsonFormat,
)
