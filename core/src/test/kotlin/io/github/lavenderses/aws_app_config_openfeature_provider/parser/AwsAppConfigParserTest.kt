package io.github.lavenderses.aws_app_config_openfeature_provider.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class AwsAppConfigParserTest {

    @InjectMocks
    private lateinit var awsAppConfigParser: AwsAppConfigParser

    @Mock
    private lateinit var buildAppConfigValue: BooleanAttributeParser

    @Spy
    private val objectMapper = ObjectMapperBuilder.build()

    @Nested
    inner class Parse {

        @Test
        fun normal() {
            // prepare
            // language=JSON
            val response = """
              {
                "enabled": true,
                "flag_value": true
              }
            """.trimIndent()
            val keyNode = objectMapper.readTree(response)
            val expected = AppConfigBooleanValue(
                /* enabled = */ true,
                /* value = */ true,
                /* jsonFormat = */ response,
            )

            doReturn(
                AppConfigBooleanValue(
                    /* enabled = */ true,
                    /* value = */ true,
                    /* jsonFormat = */ response,
                ),
            )
                .whenever(buildAppConfigValue)
                .apply(
                    /* keyNode = */ keyNode,
                )

            // do & verify
            assertThat(
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ buildAppConfigValue,
                ),
            ).isEqualTo(expected)
        }

        // TODO: Do test
        @Disabled
        @Test
        fun `key not found`() {
            // prepare
            // language=JSON
            val response = """
              {
                "enabled": true,
                "flag_value": true
              }
            """.trimIndent()

            // do
            val e = assertThrows<AppConfigValueParseException> {
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ buildAppConfigValue,
                )
            }

            // verify
            assertThat(e.evaluationResult).isEqualTo(EvaluationResult.FLAG_NOT_FOUND)
            verifyNoInteractions(buildAppConfigValue)
        }
    }
}
