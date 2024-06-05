package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config;

import java.time.Duration;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClientBuilder;

/**
 * Configuration for accessing AWS AppConfig instance via AWS AppConfig data client from AWS SDK.
 * <a href="https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/appconfigdata/AppConfigDataClient.html>
 * https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/appconfigdata/AppConfigDataClient.html</a>
 * By using this configuration, this Provider implementation connect to the Agent via the client instance.
 */
@Data
@Builder(toBuilder = true)
@ToString(callSuper = true)
public final class AwsAppConfigDataClientProxyConfig implements AwsAppConfigProxyConfig {

    @NotNull @NonNull private final Region region;

    @NotNull @NonNull private final Duration pollingDelay;

    /**
     * An {@link AppConfigDataClientBuilder} configuration method. You can configure any options it supports.
     * If this is null, no more configuration will be applied to it. Just calls {@link AppConfigDataClient#create()}.
     */
    @Nullable private final Consumer<AppConfigDataClientBuilder> configure;
}
