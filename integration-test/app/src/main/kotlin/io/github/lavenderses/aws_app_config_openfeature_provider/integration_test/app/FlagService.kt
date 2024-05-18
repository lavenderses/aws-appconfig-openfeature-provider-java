package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.app

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.linecorp.armeria.server.annotation.ConsumesJson
import com.linecorp.armeria.server.annotation.Get
import com.linecorp.armeria.server.annotation.Param
import com.linecorp.armeria.server.annotation.Post
import com.linecorp.armeria.server.annotation.ProducesJson
import com.linecorp.armeria.server.annotation.RequestConverter
import dev.openfeature.sdk.Client
import dev.openfeature.sdk.OpenFeatureAPI
import dev.openfeature.sdk.Structure
import dev.openfeature.sdk.Value
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigFeatureProvider
import io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.model.FlagValueResponse
import io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.model.ProxyType
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigAgentProxyConfig
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import java.net.URI

@ProducesJson
class FlagService {

    companion object {
        private const val APPLICATION_NAME = "app"
        private const val ENVIRONMENT_NAME = "env"
        private const val PROFILE_NAME = "profile"

        /**
         * This is just for serializing [Structure]. This is hard to serialize with Jackson.
         * https://github.com/open-feature/java-sdk/issues/483
         */
        private val OBJECT_MAPPER = ObjectMapperBuilder.build()
    }

    /**
     * Use OpenFeature Provider for AWS AppConfig with agent.
     */
    private val openFeatureAgentClient: Client

    init {
        // options (app / env / profile name) is fixed, and is also in docker-compose.yaml
        val agentOptions = AwsAppConfigClientOptions.builder()
            .applicationName(APPLICATION_NAME)
            .environmentName(ENVIRONMENT_NAME)
            .profile(PROFILE_NAME)
            .awsAppConfigProxyConfig(
                AwsAppConfigAgentProxyConfig.builder()
                    .endpoint(URI(PropertiesHelper.getProperties(AppProperties.AWS_APP_CONFIG_AGENT_ENDPOINT)))
                    .build(),
            )
            .build()
        val agentProvider = AwsAppConfigFeatureProvider(
            /* options = */ agentOptions,
        )
        val agentAPi = OpenFeatureAPI.getInstance()
            .apply {
                setProviderAndWait(agentProvider)
            }
        openFeatureAgentClient = agentAPi.client
    }

    @Get("/{proxy}/booleanFlag")
    fun getFlag(
        @Param("key") key: String,
        @Param("defaultValue") defaultValue: Boolean,
        @Param("proxy") proxy: ProxyType,
    ): FlagValueResponse<Boolean> {
        return getFlagInternal(
            key = key,
            defaultValue = defaultValue,
            agent = proxy,
        ) {
            getBooleanValue(
                /* p0 = */ key,
                /* p1 = */ defaultValue,
            )
        }
    }

    @Get("/{agent}/stringFlag")
    fun getFlag(
        @Param("key") key: String,
        @Param("defaultValue") defaultValue: String,
        @Param("agent") agent: ProxyType,
    ): FlagValueResponse<String> {
        return getFlagInternal(
            key = key,
            defaultValue = defaultValue,
            agent = agent,
        ) {
            getStringValue(
                /* p0 = */ key,
                /* p1 = */ defaultValue,
            )
        }
    }

    @Get("/{agent}/intFlag")
    fun getFlag(
        @Param("key") key: String,
        @Param("defaultValue") defaultValue: Int,
        @Param("agent") agent: ProxyType,
    ): FlagValueResponse<Int> {
        return getFlagInternal(
            key = key,
            defaultValue = defaultValue,
            agent = agent,
        ) {
            getIntegerValue(
                /* p0 = */ key,
                /* p1 = */ defaultValue,
            )
        }
    }

    @Get("/{agent}/doubleFlag")
    fun getFlag(
        @Param("key") key: String,
        @Param("defaultValue") defaultValue: Double,
        @Param("agent") agent: ProxyType,
    ): FlagValueResponse<Double> {
        return getFlagInternal(
            key = key,
            defaultValue = defaultValue,
            agent = agent,
        ) {
            getDoubleValue(
                /* p0 = */ key,
                /* p1 = */ defaultValue,
            )
        }
    }

    @ConsumesJson
    @Post("/{agent}/objectFlag")
    @RequestConverter(ObjectFlagRequestConverter::class)
    fun getFlag(
        @Param("key") key: String,
        @Param("agent") agent: ProxyType,
        defaultValue: Any,
    ): FlagValueResponse<JsonNode> {
        return getFlagInternal(
            key = key,
            defaultValue = OBJECT_MAPPER.readTree(defaultValue.toString()),
            agent = agent,
        ) {
            getObjectValue(
                /* p0 = */ key,
                /* p1 = */ Value.objectToValue(defaultValue),
            )
                .asStructure()
                .toMapRecursively()
        }
    }

    private fun <T> getFlagInternal(
        key: String,
        defaultValue: T,
        agent: ProxyType,
        getValue: Client.() -> T,
    ): FlagValueResponse<T> {
        val client = when (agent) {
            ProxyType.AGENT -> openFeatureAgentClient
        }

        val value = getValue(client)

        return FlagValueResponse(
            key = key,
            defaultValue = defaultValue,
            value = value,
        )
    }

    /**
     * This provider ensures returning Stricture with `key` -> [Value] mapping.
     * See. [io.github.lavenderses.aws_app_config_openfeature_provider.parser.ObjectAttributeParser].
     */
    private fun Structure.toMapRecursively(): JsonNode {
        val objectNode = OBJECT_MAPPER.createObjectNode()

        fun parseRecursive(node: Value): JsonNode {
            return when {
                node.isStructure -> {
                    val map = node.asStructure().asMap()
                    val jsonNode = OBJECT_MAPPER.createObjectNode()

                    map.forEach { (k, v) ->
                        val childNode = parseRecursive(v)
                        jsonNode.putIfAbsent(k, childNode)
                    }

                    jsonNode
                }
                node.isList -> {
                    val map = node.asList()
                    val jsonNode = OBJECT_MAPPER.createArrayNode()

                    map.forEach {
                        val childNode = parseRecursive(it)
                        jsonNode.add(childNode)
                    }

                    jsonNode
                }
                node.isNull -> OBJECT_MAPPER.createObjectNode()
                node.isBoolean -> BooleanNode.valueOf(node.asBoolean())
                node.isString -> TextNode.valueOf(node.asString())
                node.isNumber -> IntNode.valueOf(node.asInteger())
                node.isInstant -> TextNode.valueOf(node.asInstant().toString())
                else -> OBJECT_MAPPER.createObjectNode()
            }
        }

        val root = asMap()
        root.forEach { (k, v) ->
            val childNode = parseRecursive(v)
            objectNode.putIfAbsent(k, childNode)
        }

        return objectNode
    }
}
