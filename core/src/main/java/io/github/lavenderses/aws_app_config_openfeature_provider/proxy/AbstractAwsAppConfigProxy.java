package io.github.lavenderses.aws_app_config_openfeature_provider.proxy;

import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigProxyConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;

import static java.util.Objects.isNull;

/**
 * Common utility class for {@link AwsAppConfigProxy} implementation. This class handles common process of getting
 * feature flag value from AWS AppConfig instance.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAwsAppConfigProxy implements AwsAppConfigProxy {

    private static final Logger log = LoggerFactory.getLogger(AbstractAwsAppConfigProxy.class);

    @NotNull
    @NonNull
    protected final AwsAppConfigProxyConfig awsAppConfigProxyConfig;

    /**
     * @param key feature flag key in OpenFeature world
     * @param response SDK response object
     * @return JSON string when repose is valid, otherwise null
     */
    @Language("json")
    @Nullable
    protected final String extractResponseBody(
        @NotNull final String key,
        @NotNull final GetLatestConfigurationResponse response
    ) {
        final SdkBytes configuration = response.configuration();
        if (isNull(configuration) || configuration.asByteArray().length == 0) {
            log.info("Flag value from AppConfig with key {} does not found.", key);

            return null;
        }

        return configuration.asUtf8String();
    }
}
