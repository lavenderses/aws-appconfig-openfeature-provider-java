package io.github.lavenderses.aws_app_config_openfeature_provider.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import dev.openfeature.sdk.ImmutableStructure
import dev.openfeature.sdk.Value
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigObjectValue
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
class ObjectAttributeParserTest {

    companion object {
        private val OBJECT_MAPPER = ObjectMapperBuilder.build()
    }

    @InjectMocks
    private lateinit var objectAttributeParser: ObjectAttributeParser

    @Spy
    private val objectMapper = ObjectMapperBuilder.build()

    @Nested
    inner class AttributeAsObject {

        @Test
        fun normal() {
            // prepare
            val keyNode = OBJECT_MAPPER.readTree(
                // language=JSON
                """
                  {
                    "enabled": true,
                    "flag_value": "{\"foo\":{\"bar\":\"buz\"},\"qux\":[\"quux\",\"corge\"]}"
                  }
                """.trimIndent(),
            )
            val expected = AppConfigObjectValue(
                /* enabled = */ true,
                /* value = */ Value(
                    ImmutableStructure(
                        mapOf(
                            "foo" to Value(
                                ImmutableStructure(
                                    mapOf(
                                        "bar" to Value("buz"),
                                    ),
                                ),
                            ),
                            "qux" to Value(
                                listOf(
                                    Value("quux"),
                                    Value("corge"),
                                ),
                            ),
                        ),
                    ),
                ),
                @Suppress("MaxLineLength")
                /* jsonFormat = */ """{"enable":true,"flag_value":"{\"foo\":{\"bar\":\"buz\"},\"qux\":[\"quux\",\"corge\"]}"}""",
            )

            // do & verify
            assertThat(
                objectAttributeParser.apply(
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
                    "flag_value": "{\"foo\":{\"bar\":\"buz\"},\"qux\":[\"quux\",\"corge\"]}"
                  }
                """.trimIndent(),
            )
            val expected = AppConfigObjectValue(
                /* enabled = */ false,
                /* value = */ Value(
                    ImmutableStructure(
                        mapOf(
                            "foo" to Value(
                                ImmutableStructure(
                                    mapOf(
                                        "bar" to Value("buz"),
                                    ),
                                ),
                            ),
                            "qux" to Value(
                                listOf(
                                    Value("quux"),
                                    Value("corge"),
                                ),
                            ),
                        ),
                    ),
                ),
                @Suppress("MaxLineLength")
                /* jsonFormat = */ """{"enable":true,"flag_value":"{\"foo\":{\"bar\":\"buz\"},\"qux\":[\"quux\",\"corge\"]}"}""",
            )

            // do & verify
            assertThat(
                objectAttributeParser.apply(
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
                objectAttributeParser.apply(
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
                objectAttributeParser.convertJsonNodeAsValueRecursively(
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
                objectAttributeParser.convertJsonNodeAsValueRecursively(
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
                objectAttributeParser.convertJsonNodeAsValueRecursively(
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
                objectAttributeParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `primitive structure`() {
            // prepare
            val valueNode = OBJECT_MAPPER.readTree(
                // language=JSON
                """
                  {
                    "foo": {
                      "bar": {
                        "qux": 12345
                      },
                      "quux": true
                    },
                    "corge": "98765",
                    "grault": "${Time.FIXED_TIME}"
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
                                                "qux" to Value(12345),
                                            ),
                                        ),
                                    ),
                                    "quux" to Value(true),
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
                objectAttributeParser.convertJsonNodeAsValueRecursively(
                    valueNode,
                    hashMap,
                    hashMap::put,
                )
                    .asStructure()
                    .asObjectMap(),
            ).isEqualTo(expected.asStructure().asObjectMap())
        }
    }
}
