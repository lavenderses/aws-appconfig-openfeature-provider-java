package io.github.lavenderses.aws_app_config_openfeature_provider

import kotlin.reflect.KClass

fun KClass<AwsAppConfigClientOptions>.fixture(): AwsAppConfigClientOptions = AwsAppConfigClientOptions.builder()
    .applicationName("app")
    .environmentName("env")
    .profile("profile")
    .build()
