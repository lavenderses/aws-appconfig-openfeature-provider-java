package io.github.lavenderses.aws_app_config_openfeature_provider;

import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigProxyConfig;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

/**
 * Client options for {@link AppConfigDataClient} required in this OpenFeature Provider implementation.
 */
@Data
@Builder(toBuilder = true)
@ToString
public final class AwsAppConfigClientOptions {

    @NotNull @NonNull private final String applicationName;

    @NotNull @NonNull private final String environmentName;

    @NotNull @NonNull private final String profile;

    @NotNull @NonNull private final AwsAppConfigProxyConfig awsAppConfigProxyConfig;
}
