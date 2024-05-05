package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AwsAppConfigParserTest {

    @InjectMocks
    private lateinit var awsAppConfigParser: AwsAppConfigParser

    @Spy
    private val objectMapper = ObjectMapperBuilder.build()

    @Nested
    inner class Boolean {

        @Test
        fun normal() {
            // prepare
            // language=JSON
            val response = """
              {
                "key": {
                  "enabled": true,
                  "flag_value": true
                }
              }
            """.trimIndent()
            val expected = AppConfigBooleanValue(
                /* enabled = */ true,
                /* value = */ true,
                /* jsonFormat = */ response,
            )

            println(expected)
            // do & verify
            assertThat(
                awsAppConfigParser.parseAsBooleanValue(
                    /* key = */ "key",
                    /* value = */ response,
                ).also { println(it) },
            ).isEqualTo(expected)
        }

        @Test
        fun `enable is false`() {
            // prepare
            // language=JSON
            val response = """
              {
                "key": {
                  "enabled": false,
                  "flag_value": true
                }
              }
            """.trimIndent()
            val expected = AppConfigBooleanValue(
                /* enabled = */ false,
                /* value = */ true,
                /* jsonFormat = */ response,
            )

            // do & verify
            assertThat(
                awsAppConfigParser.parseAsBooleanValue(
                    /* key = */ "key",
                    /* value = */ response,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `flag_value is null`() {
            // prepare
            // language=JSON
            val response = """
              {
                "key": {
                  "enabled": true
                }
              }
            """.trimIndent()

            // do
            val e = assertThrows<AppConfigValueParseException> {
                awsAppConfigParser.parseAsBooleanValue(
                    /* key = */ "key",
                    /* value = */ response,
                )
            }

            // verify
            assertThat(e.evaluationResult).isEqualTo(EvaluationResult.INVALID_ATTRIBUTE_FORMAT)
        }

        @Test
        fun `key not found`() {
            // prepare
            // language=JSON
            val response = """
              {
                "invalid key": {
                  "enabled": true,
                  "flag_value": true
                }
              }
            """.trimIndent()

            // do
            val e = assertThrows<AppConfigValueParseException> {
                awsAppConfigParser.parseAsBooleanValue(
                    /* key = */ "key",
                    /* value = */ response,
                )
            }

            // verify
            assertThat(e.response).isEqualTo(response)
            assertThat(e.evaluationResult).isEqualTo(EvaluationResult.FLAG_NOT_FOUND)
        }
    }
}
