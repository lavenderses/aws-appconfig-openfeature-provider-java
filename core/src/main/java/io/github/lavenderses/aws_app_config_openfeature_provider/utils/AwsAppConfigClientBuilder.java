package io.github.lavenderses.aws_app_config_openfeature_provider.utils;

import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

public class AwsAppConfigClientBuilder {

    @NotNull
    public static AppConfigDataClient build(@NotNull final AwsAppConfigClientOptions options) {
        return AppConfigDataClient.create();
    }
}
