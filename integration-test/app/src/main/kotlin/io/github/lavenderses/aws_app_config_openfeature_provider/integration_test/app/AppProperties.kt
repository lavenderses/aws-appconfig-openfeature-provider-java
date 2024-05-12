package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.app

/**
 * Represents application variable.
 * This is resolved in following priority order.
 *
 * 1. value from [env]
 * 2. value from [properties]
 * 3. [defaultValue]
 */
enum class AppProperties(
    /** Key of Java properties. */
    val properties: String,
    /** Environment variable. */
    val env: String,
    /** Default value for fallback. */
    val defaultValue: String,
) {

    /** Endpoint for accessing AWS AppConfig agent. */
    AWS_APP_CONFIG_AGENT_ENDPOINT(
        properties = "appconfig_endpoint",
        env = "APP_CONFIG_ENDPOINT",
        defaultValue = "http://localhost:2772",
    ),
}
