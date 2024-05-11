package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.model

data class FlagValueResponse<T>(
    val key: String,
    val defaultValue: T,
    val value: T,
)
