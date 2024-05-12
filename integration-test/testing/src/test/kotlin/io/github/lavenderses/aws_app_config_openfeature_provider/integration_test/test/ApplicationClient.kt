package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.linecorp.armeria.client.RestClient
import io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.model.FlagValueResponse
import io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.model.ProxyType
import org.intellij.lang.annotations.Language
import java.net.URI

class ApplicationClient {

    companion object {

        /**
         * Test application's endpoint. This is configured in docker-compose file, and port is Spring boot default
         * value. See also /integration-test/docker/docker-compose.yaml for configuration.
         */
        private val ENDPOINT = URI("http://localhost:8080")

        private val OBJECT_MAPPER = ObjectMapper()
    }

    private val client = RestClient.of(ENDPOINT)

    @Language("json")
    fun <T : Any> getJsonBody(
        agent: ProxyType,
        flagType: FlagType,
        key: T,
        defaultValue: Any,
    ): String {
        val flag = client.get("/{agent}/{flagType}")
            .pathParam("agent", agent)
            .pathParam("flagType", flagType.path)
            .queryParam("key", key)
            .queryParam("defaultValue", defaultValue)
            .header("Accept", "application/json")
            .execute(FlagValueResponse::class.java)
            .get()
            .content()
        return OBJECT_MAPPER.writeValueAsString(flag)
    }

    @Language("json")
    fun getJsonBody(
        agent: ProxyType,
        key: String,
        defaultValue: Any,
    ): String {
        val flag = client.post("/{agent}/objectFlag")
            .pathParam("agent", agent)
            .queryParam("key", key)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .contentJson(defaultValue)
            .execute(FlagValueResponse::class.java)
            .get()
            .content()
        return OBJECT_MAPPER.writeValueAsString(flag)
    }
}
