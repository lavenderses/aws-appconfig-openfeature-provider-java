package io.github.lavenderses.aws_app_config_openfeature_provider.proxy

import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.agent.AwsAppConfigAgentProxy
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigAgentProxyConfig
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigProxyConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI

class AwsAppConfigProxyBuilderTest {

    @Test
    fun appConfigAgentProxyConfig() {
        // prepare
        val config = AwsAppConfigAgentProxyConfig.builder()
            .endpoint(URI("http://localhost:8080"))
            .build()
        val option = AwsAppConfigClientOptions.builder()
            .awsAppConfigProxyConfig(config)
            .applicationName("app")
            .environmentName("env")
            .profile("profile")
            .build()

        // do & verify
        assertThat(
            AwsAppConfigProxyBuilder.build(
                /* options = */ option,
            ),
        ).isInstanceOf(AwsAppConfigAgentProxy::class)
    }

    @Test
    fun unknownConfig() {
        // prepare
        val config = object : AwsAppConfigProxyConfig {}
        val option = AwsAppConfigClientOptions.builder()
            .awsAppConfigProxyConfig(config)
            .applicationName("app")
            .environmentName("env")
            .profile("profile")
            .build()

        // do & verify
        assertThrows<IllegalArgumentException> {
            AwsAppConfigProxyBuilder.build(
                /* options = */ option,
            )
        }
    }
}
