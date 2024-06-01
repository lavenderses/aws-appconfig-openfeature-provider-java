package io.github.lavenderses.aws_app_config_openfeature_provider.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigDoubleValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DoubleAttributeParserTest {

    companion object {
        private val OBJECT_MAPPER = ObjectMapperBuilder.build()
    }

    @InjectMocks
    private lateinit var doubleAttributeParser: DoubleAttributeParser

    @Test
    fun normal() {
        // prepare
        val keyNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "enabled": true,
                 "flag_value": 12345.0
              }
            """.trimIndent(),
        )
        val expected =
            AppConfigDoubleValue(
                /* enabled = */ true,
                /* value = */ 12345.0,
                /* jsonFormat = */ """{"enable":true,"flag_value":12345.0}""",
            )

        // do & verify
        assertThat(
            doubleAttributeParser.apply(
                /* keyNode = */ keyNode,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun `enable is false`() {
        // prepare
        val keyNode = OBJECT_MAPPER.readTree(
            // language=JSON
            """
              {
                "enabled": false,
                "flag_value": 12345.0
              }
            """.trimIndent(),
        )
        val expected =
            AppConfigDoubleValue(
                /* enabled = */ false,
                /* value = */ 12345.0,
                /* jsonFormat = */ """{"enable":true,"flag_value":12345.0}""",
            )

        // do & verify
        assertThat(
            doubleAttributeParser.apply(
                /* keyNode = */ keyNode,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun `flag_value is null`() {
        // prepare
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
            doubleAttributeParser.apply(
                /* keyNode = */ keyNode,
            )
        }

        // verify
        assertThat(e.evaluationResult).isEqualTo(EvaluationResult.INVALID_ATTRIBUTE_FORMAT)
    }
}
