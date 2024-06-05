package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config;

import java.net.URI;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for accessing AWS AppConfig instance via AWS AppConfig agent.
 * For what is AWS AppConfig anget, see this document. In short, it is like a side-car container.
 * <a href="https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-integration-containers-agent.html"
 * >https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-integration-containers-agent.html</a>
 * By using this configuration, this Provider implementation connect to the agent to get feature flag value.
 */
@Data
@Builder(toBuilder = true)
@ToString(callSuper = true)
public final class AwsAppConfigAgentProxyConfig implements AwsAppConfigProxyConfig {

    /**
     * An endpoint for accessing the AWS AppConfig agent.
     * For example, if you are using it as Kubernetes pod's side-car container with default value, this would be
     * {@code https://localhost:2772}.
     */
    @NotNull @NonNull private final URI endpoint;
}
