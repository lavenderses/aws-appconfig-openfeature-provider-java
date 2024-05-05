package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import dev.openfeature.sdk.ImmutableStructure
import dev.openfeature.sdk.Value
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.helper.Time
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
    inner class Parse {

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

            // do & verify
            assertThat(
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ awsAppConfigParser::attributeAsBoolean,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `key not found`() {
            // prepare
            // language=JSON
            val response = """
              {
                "invalid_key": {
                  "enabled": true,
                  "flag_value": true
                }
              }
            """.trimIndent()

            // do
            val e = assertThrows<AppConfigValueParseException> {
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ awsAppConfigParser::attributeAsBoolean,
                )
            }

            // verify
            assertThat(e.evaluationResult).isEqualTo(EvaluationResult.FLAG_NOT_FOUND)
        }
    }

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

            // do & verify
            assertThat(
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ awsAppConfigParser::attributeAsBoolean,
                ),
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
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ awsAppConfigParser::attributeAsBoolean,
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
                awsAppConfigParser.parse(
                    /* key = */ "key",
                    /* value = */ response,
                    /* buildAppConfigValue = */ awsAppConfigParser::attributeAsBoolean,
                )
            }

            // verify
            assertThat(e.evaluationResult).isEqualTo(EvaluationResult.INVALID_ATTRIBUTE_FORMAT)
        }
    }

    @Nested
    inner class AttributeAsObject {

        @Test
        fun normal() {
            // prepare
            val responseNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "key": {
                      "enabled": true,
                      "flag_value": 12345
                    }
                  }
                """.trimIndent(),
            )
            val keyNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "enabled": true,
                    "flag_value": 12345
                  }
                """.trimIndent(),
            )
            val expected = AppConfigObjectValue(
                /* enabled = */ true,
                /* value = */ Value(12345),
                /* jsonFormat = */ """{"key":{"enable":true,"flag_value":12345}}""",
            )

            // do & verify
            assertThat(
                awsAppConfigParser.attributeAsObject(
                    /* responseNode = */ responseNode,
                    /* keyNode = */ keyNode,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `enable is false`() {
            // prepare
            val responseNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "key": {
                      "enabled": false,
                      "flag_value": 12345
                    }
                  }
                """.trimIndent(),
            )
            val keyNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "enabled": false,
                    "flag_value": 12345
                  }
                """.trimIndent(),
            )
            val expected = AppConfigObjectValue(
                /* enabled = */ false,
                /* value = */ Value(12345),
                /* jsonFormat = */ """{"key":{"enable":false,"flag_value":12345}}""",
            )

            // do & verify
            assertThat(
                awsAppConfigParser.attributeAsObject(
                    /* responseNode = */ responseNode,
                    /* keyNode = */ keyNode,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `flag_value is null`() {
            // prepare
            val responseNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "key": {
                      "enabled": true
                    }
                  }
                """.trimIndent(),
            )
            val keyNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "enabled": true
                  }
                """.trimIndent(),
            )

            // do
            val e = assertThrows<AppConfigValueParseException> {
                awsAppConfigParser.attributeAsObject(
                    /* responseNode = */ responseNode,
                    /* keyNode = */ keyNode,
                )
            }

            // verify
            assertThat(e.evaluationResult).isEqualTo(EvaluationResult.INVALID_ATTRIBUTE_FORMAT)
        }
    }

    @Nested
    inner class ConvertJsonNodeAsValueRecursively {

        @Test
        fun `primitive boolean`() {
            // prepare
            val valueNode = BooleanNode.valueOf(true)
            val hashMap = mutableMapOf<String, Value>()
            val expected = Value(true)

            // do & verify
            assertThat(
                awsAppConfigParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `primitive number`() {
            // prepare
            val valueNode = IntNode(1)
            val hashMap = mutableMapOf<String, Value>()
            val expected = Value(1)

            // do & verify
            assertThat(
                awsAppConfigParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `primitive string`() {
            // prepare
            val valueNode = TextNode("text")
            val hashMap = mutableMapOf<String, Value>()
            val expected = Value("text")

            // do & verify
            assertThat(
                awsAppConfigParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `primitive datetime`() {
            // prepare
            val valueNode = TextNode(Time.FIXED_TIME)
            val hashMap = mutableMapOf<String, Value>()
            val expected = Value(Time.fixedInstant)

            // do & verify
            assertThat(
                awsAppConfigParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `primitive structure`() {
            // prepare
            val valueNode = objectMapper.readTree(
                // language=JSON
                """
                  {
                    "flag_value": {
                      "foo": {
                        "bar": {
                          "qux": 12345
                        },
                        "quux": true
                      },
                      "corge": "98765",
                      "grault": "${Time.FIXED_TIME}"
                    }
                  }
                """.trimIndent(),
            )
            val hashMap = mutableMapOf<String, Value>()
            val expected = Value(
                ImmutableStructure(
                    mutableMapOf(
                        "foo" to Value(
                            ImmutableStructure(
                                mutableMapOf(
                                    "bar" to Value(
                                        ImmutableStructure(
                                            mutableMapOf(
                                                "qux" to Value(123245),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        "corge" to Value("98765"),
                        "grault" to Value(Time.fixedInstant),
                    ),
                ),
            )

            // do & verify
            assertThat(
                awsAppConfigParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                ),
            ).isEqualTo(expected)
        }
    }
}
