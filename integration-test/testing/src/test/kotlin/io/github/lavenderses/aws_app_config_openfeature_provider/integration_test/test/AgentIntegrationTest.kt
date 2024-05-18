package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.test

import io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.model.ProxyType
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class AgentIntegrationTest {

    private val client = ApplicationClient()

    @Test
    fun string() {
        // prepare
        val key = "string"
        val defaultValue = "default"
        // see /integration-test/docker/app:env:profile
        // language=json
        val expected = """
          {
            "key": "string",
            "defaultValue": "default",
            "value": "VALUE"
          }
        """.trimIndent()

        // do
        val actual = client.getJsonBody(
            key = key,
            flagType = FlagType.STRING,
            defaultValue = defaultValue,
            agent = ProxyType.AGENT,
        )

        // verify
        JSONAssert.assertEquals(
            /* expectedStr = */ expected,
            /* actualStr = */ actual,
            /* strict = */ true,
        )
    }

    @Test
    fun boolean() {
        // prepare
        val key = "boolean"
        val defaultValue = "false"
        // see /integration-test/docker/app:env:profile
        // language=json
        val expected = """
          {
            "key": "boolean",
            "defaultValue": false,
            "value": true
          }
        """.trimIndent()

        // do
        val actual = client.getJsonBody(
            key = key,
            flagType = FlagType.BOOLEAN,
            defaultValue = defaultValue,
            agent = ProxyType.AGENT,
        )

        // verify
        JSONAssert.assertEquals(
            /* expectedStr = */ expected,
            /* actualStr = */ actual,
            /* strict = */ true,
        )
    }

    @Test
    fun int() {
        // prepare
        val key = "int"
        val defaultValue = "-1"
        // see /integration-test/docker/app:env:profile
        // language=json
        val expected = """
          {
            "key": "int",
            "defaultValue": -1,
            "value": -1
          }
        """.trimIndent()

        // do
        val actual = client.getJsonBody(
            key = key,
            flagType = FlagType.INT,
            defaultValue = defaultValue,
            agent = ProxyType.AGENT,
        )

        // verify
        JSONAssert.assertEquals(
            /* expectedStr = */ expected,
            /* actualStr = */ actual,
            /* strict = */ true,
        )
    }

    @Test
    fun double() {
        // prepare
        val key = "double"
        val defaultValue = "0.0"
        // see /integration-test/docker/app:env:profile
        // language=json
        val expected = """
          {
            "key": "double",
            "defaultValue": 0.0,
            "value": 98765.432
          }
        """.trimIndent()

        // do
        val actual = client.getJsonBody(
            key = key,
            flagType = FlagType.DOUBLE,
            defaultValue = defaultValue,
            agent = ProxyType.AGENT,
        )

        // verify
        JSONAssert.assertEquals(
            /* expectedStr = */ expected,
            /* actualStr = */ actual,
            /* strict = */ true,
        )
    }

    @Test
    fun objectValue() {
        // prepare
        val key = "objectValue"
        val defaultValue = "{}"
        // see /integration-test/docker/app:env:profile
        // language=json
        val expected = """
          {
            "key": "objectValue",
            "defaultValue": {},
            "value": {
              "nested": {
                "this is": "value"
              },
              "normal": "value"
            }
          }
        """.trimIndent()

        // do
        val actual = client.getJsonBody(
            key = key,
            defaultValue = defaultValue,
            agent = ProxyType.AGENT,
        )

        // verify
        JSONAssert.assertEquals(
            /* expectedStr = */ expected,
            /* actualStr = */ actual,
            /* strict = */ true,
        )
    }
}
