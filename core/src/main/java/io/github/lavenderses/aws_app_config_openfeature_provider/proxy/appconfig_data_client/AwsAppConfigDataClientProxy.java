package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.AbstractAwsAppConfigProxy;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigDataClientProxyConfig;
import jakarta.annotation.PreDestroy;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Provider implementation proxy for accessing AWS AppConfig instance via AWS AppConfig client from SDK.
 * <br/>
 * You can use this with the client instance you prepared. Or just pass the configuration class, then this class creates
 * and manages it.
 */
public final class AwsAppConfigDataClientProxy extends AbstractAwsAppConfigProxy {

    private final CachedFeatureFlagManager cachedFeatureFlagManager;

    @VisibleForTesting
    AwsAppConfigDataClientProxy(
        @NotNull final CachedFeatureFlagManager cachedFeatureFlagManager
    ) {
        super();

        this.cachedFeatureFlagManager = requireNonNull(cachedFeatureFlagManager, "cachedFeatureFlagManager");
    }

    /**
     * Constructor for configuring {@link AppConfigDataClient} with your own configuration.
     * <br/>
     * When you use this constructor, this provider implementation library is responsible for closing
     * {@link AppConfigDataClient} resource. You don't have to worry about.
     *
     * @param config a configuration to {@link AppConfigDataClient}
     */
    public AwsAppConfigDataClientProxy(
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final AwsAppConfigDataClientProxyConfig config
    ) {
        super();

        cachedFeatureFlagManager = new CachedFeatureFlagManager(
            /* options = */ options,
            /* config = */ config,
            /* startSession = */ true
        );
    }

    @PreDestroy
    @Override
    public void close() throws Exception {
        cachedFeatureFlagManager.close();
    }

    /**
     * Get the feature flag value as JSON schema (see {@link AppConfigValue} for the schema).<br/>
     * Note that return value is not validated, this is just raw value from AWS AppConfig.
     *
     * @param key feature flag key
     * @return JSON schema response from AWS AppConfigInstance
     */
    @Language("json")
    @Nullable
    @Override
    public String getRawFlagObject(@NotNull final String key) {
        final JsonNode featureFlagJsonValue = cachedFeatureFlagManager.getCachedFeatureFlagByKeyFrom(
            /* key = */ requireNonNull(key, "key")
        );

        if (isNull(featureFlagJsonValue)) {
            return null;
        } else {
            return featureFlagJsonValue.toString();
        }
    }
}
