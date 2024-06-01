package io.github.lavenderses.aws_app_config_openfeature_provider.proxy;

import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.agent.AwsAppConfigAgentProxy;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client.AwsAppConfigDataClientProxy;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigAgentProxyConfig;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigDataClientProxyConfig;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigProxyConfig;
import org.jetbrains.annotations.NotNull;

public final class AwsAppConfigProxyBuilder {

    @NotNull
    public static AwsAppConfigProxy build(
        @NotNull final AwsAppConfigClientOptions options
        ) {
        final AwsAppConfigProxyConfig config = options.getAwsAppConfigProxyConfig();

        if (config instanceof AwsAppConfigAgentProxyConfig) {
            return new AwsAppConfigAgentProxy(
                /* option = */ options,
                /* config = */ (AwsAppConfigAgentProxyConfig) config
            );
        } else if (config instanceof AwsAppConfigDataClientProxyConfig) {
            return new AwsAppConfigDataClientProxy(
                /* options = */ options,
                /* config = */ (AwsAppConfigDataClientProxyConfig) config
            );
        } else {
            throw new IllegalArgumentException(String.format("unknown type of config: %s", config));
        }
    }
}
