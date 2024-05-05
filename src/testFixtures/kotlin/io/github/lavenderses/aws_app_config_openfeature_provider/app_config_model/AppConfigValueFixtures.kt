package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import com.fasterxml.jackson.databind.JsonNode
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import kotlin.reflect.KClass

private val objectMapper = ObjectMapperBuilder.build()

fun KClass<String>.appConfigResponse(
    keyName: String = "key_name",
    enabled: Boolean = true,
    value: Any? = true,
): String {
    // language=JSON
    return """
      {
        "$keyName": {
          "enabled": $enabled,
          "flag_value": $value
        }
      }
    """.trimIndent()
}

fun KClass<JsonNode>.appConfigResponse(
    keyName: String = "key_name",
    enabled: Boolean = true,
    value: Any = true,
): JsonNode = objectMapper.readTree(
    String::class.appConfigResponse(
        keyName = keyName,
        enabled = enabled,
        value = value,
    ),
)

fun KClass<JsonNode>.appConfigResponse(
    jsonNodeString: String = String::class.appConfigResponse(),
): JsonNode = objectMapper.readTree(jsonNodeString)
