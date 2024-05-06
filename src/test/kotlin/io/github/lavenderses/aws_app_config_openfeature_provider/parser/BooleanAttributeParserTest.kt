package io.github.lavenderses.aws_app_config_openfeature_provider.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BooleanAttributeParserTest {

    companion object {
        private val OBJECT_MAPPER = ObjectMapperBuilder.build()
    }

    @InjectMocks
    private lateinit var booleanAttributeParser: BooleanAttributeParser

    @Test
    fun normal() {
        // prepare
        val responseNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "key": {
                  "enabled": true,
                  "flag_value": true
                }
              }
            """.trimIndent(),
        )
        val keyNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "enabled": true,
                "flag_value": true
              }
            """.trimIndent(),
        )
        val expected = AppConfigBooleanValue(
            /* enabled = */ true,
            /* value = */ true,
            /* jsonFormat = */ """{"key":{"enable":true,"flag_value":true}}""",
        )

        // do & verify
        assertThat(
            booleanAttributeParser.apply(
                /* responseNode = */ responseNode,
                /* keyNode = */ keyNode,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun `enable is false`() {
        // prepare
        // language=JSON
        val responseNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "key": {
                  "enabled": false,
                  "flag_value": true
                }
              }
            """.trimIndent(),
        )
        val keyNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "enabled": false,
                "flag_value": true
              }
            """.trimIndent(),
        )
        val expected = AppConfigBooleanValue(
            /* enabled = */ false,
            /* value = */ true,
            /* jsonFormat = */ """{"key":{"enable":true,"flag_value":false}}""",
        )

        // do & verify
        assertThat(
            booleanAttributeParser.apply(
                /* responseNode = */ responseNode,
                /* keyNode = */ keyNode,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun `flag_value is null`() {
        // prepare
        val responseNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "key": {
                  "enabled": true
                }
              }
            """.trimIndent(),
        )
        val keyNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "enabled": true
              }
            """.trimIndent(),
        )

        // do
        val e = assertThrows<AppConfigValueParseException> {
            booleanAttributeParser.apply(
                /* responseNode = */ responseNode,
                /* keyNode = */ keyNode,
            )
        }

        // verify
        assertThat(e.evaluationResult).isEqualTo(EvaluationResult.INVALID_ATTRIBUTE_FORMAT)
    }
}
