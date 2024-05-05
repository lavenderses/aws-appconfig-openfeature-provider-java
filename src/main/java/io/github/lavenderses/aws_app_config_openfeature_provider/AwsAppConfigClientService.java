package io.github.lavenderses.aws_app_config_openfeature_provider;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValueParseException;
import io.github.lavenderses.aws_app_config_openfeature_provider.converter.AppConfigValueConverter;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ErrorEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;

import java.util.concurrent.atomic.AtomicReference;

import io.github.lavenderses.aws_app_config_openfeature_provider.utils.AwsAppConfigClientBuilder;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AwsAppConfigParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;

/**
 * Service layer class for AWS AppConfig.
 */
final class AwsAppConfigClientService {

    private static final Logger log = LoggerFactory.getLogger(AwsAppConfigClientService.class);

    @NotNull
    private final AtomicReference<AwsAppConfigState> appConfigState = new AtomicReference<>(AwsAppConfigState.NONE);

    @NotNull
    private final AppConfigDataClient client;

    @NotNull
    private final AwsAppConfigClientOptions options;

    @NotNull
    private final AwsAppConfigParser awsAppConfigParser;

    @NotNull
    private final AppConfigValueConverter appConfigValueConverter;

    /**
     * This is just for mockito JUnit extension.
     * Locate this constructor on top on constructors so that mockito can mock all fields in
     * {@link AwsAppConfigClientService}.
     */
    @VisibleForTesting
    AwsAppConfigClientService(
        @NotNull final AppConfigDataClient client,
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final AwsAppConfigParser awsAppConfigParser,
        @NotNull final AppConfigValueConverter appConfigValueConverter
    ) {
        this.client = requireNonNull(client, "AppConfigDataClient");
        this.options = requireNonNull(options, "AwsAppConfigClientOptions");
        this.awsAppConfigParser = requireNonNull(awsAppConfigParser, "AwsAppConfigParse");
        this.appConfigValueConverter = requireNonNull(appConfigValueConverter, "appConfigValueConverter");
    }

    /**
     * Set up {@link AppConfigDataClient}, which is AWS AppConfig client with parameters. All client
     * options for this client instance is configured as {@link AwsAppConfigClientOptions} and passed
     * via constructor.
     * Docs are available in here.<br>
     * <a href="https://sdk.amazonaws.com/java/api/2.25.45/software/amazon/awssdk/services/appconfig/AppConfigClient.html">
     * AppConfigClient (AWS SDK for Java - 2.25.45)</a>
     */
    AwsAppConfigClientService(@NotNull final AwsAppConfigClientOptions options) {
        log.info("Initializing AWS AppConfig with config: {}", options);

        this.options = requireNonNull(options, "awsAppConfigClientOptions");
        client = AwsAppConfigClientBuilder.build(options);
        awsAppConfigParser = new AwsAppConfigParser();
        appConfigValueConverter = new AppConfigValueConverter();
    }

    @NotNull
    AwsAppConfigState state() {
        return appConfigState.get();
    }

    /**
     * Get Boolean type feature flag from AppConfig by {@param key}.
     */
    @NotNull
    EvaluationValue<Boolean> getBoolean(
        @NotNull final String key,
        @NotNull final Boolean defaultValue
    ) {
        // Get configuration from AWS AppConfig via SDK
        final GetLatestConfigurationRequest request = GetLatestConfigurationRequest.builder()
            .configurationToken(options.getApplicationName())
            .build();

        final GetLatestConfigurationResponse response;
        try {
            response = client.getLatestConfiguration(request);
        } catch (final Exception e) {
            log.error("Failed to get response from AppConfig.", e);

            return new ErrorEvaluationValue<>(
                /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
                /* errorMessage = */ null,
                /* reason = */ Reason.DEFAULT
            );
        }

        final String responseBody = extractResponseBody(
            /* key = */ key,
            /* response = */ response
        );
        if (isNull(responseBody)) {
            return parseErrorEvaluationValue(
                /* errorMessage = */ null
            );
        }

        // Parse SDK response as boolean feature flag
        final AppConfigBooleanValue flagValue;
        try {
            flagValue = awsAppConfigParser.parseAsBooleanValue(
                /* key = */ key,
                /* value = */ responseBody
            );
        } catch (final AppConfigValueParseException e) {
            log.error("Failed to parse object from AWS AppConfig response. Fall back to default flag value", e);
            return e.asErrorEvaluationResult();
        }
        log.info("Flag value [{}: {}] fetched.", key, flagValue.getValue());

        return appConfigValueConverter.toEvaluationValue(
            /* defaultValue = */ defaultValue,
            /* appConfigValue = */ flagValue,
            // true because this is boolean flag
            /* asPrimitive = */ true
        );
    }

    @NotNull
    EvaluationValue<String> getString(@NotNull final String key) {
      return null;
    }

    @NotNull
    EvaluationValue<Integer> getInteger(@NotNull final String key) {
      return null;
    }

    @NotNull
    EvaluationValue<Double> getDouble(@NotNull final String key) {
      return null;
    }

    @NotNull
    EvaluationValue<Value> getValue(@NotNull final String key) {
      return null;
    }

    /**
     * @param key feature flag key in OpenFeature world
     * @param response SDK response object
     * @return JSON string when repose is valid, otherwise null
     */
    @Nullable
    @VisibleForTesting
    String extractResponseBody(
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

    private <T> EvaluationValue<T> parseErrorEvaluationValue(@Nullable final String errorMessage) {
        return new ErrorEvaluationValue<>(
            /* errorCode = */ ErrorCode.PARSE_ERROR,
            /* errorMessage = */ errorMessage,
            /* reason = */ Reason.ERROR);
    }
}
