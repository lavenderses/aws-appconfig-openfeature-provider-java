package io.github.lavenderses.aws_app_config_openfeature_provider;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.AppConfigValueParseException;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.AwsAppConfigParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.converter.AppConfigValueConverter;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ErrorEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.BooleanAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.ObjectAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.AwsAppConfigClientBuilder;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

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

    @NotNull
    private final BooleanAttributeParser booleanAttributeParser;

    @NotNull
    private final ObjectAttributeParser objectAttributeParser;

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
        @NotNull final AppConfigValueConverter appConfigValueConverter,
        @NotNull final BooleanAttributeParser booleanAttributeParser,
        @NotNull final ObjectAttributeParser objectAttributeParser
    ) {
        this.client = requireNonNull(client, "AppConfigDataClient");
        this.options = requireNonNull(options, "AwsAppConfigClientOptions");
        this.awsAppConfigParser = requireNonNull(awsAppConfigParser, "AwsAppConfigParse");
        this.appConfigValueConverter = requireNonNull(appConfigValueConverter, "appConfigValueConverter");
        this.booleanAttributeParser = requireNonNull(booleanAttributeParser, "booleanAttributeParser");
        this.objectAttributeParser = requireNonNull(objectAttributeParser, "objectAttributeParser");
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
        booleanAttributeParser = new BooleanAttributeParser();
        objectAttributeParser = new ObjectAttributeParser();
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
        try {
            return getInternal(
                /* key = */ key,
                /* defaultValue = */ defaultValue,
                /* asPrimitive = */ true,
                /* parseFromResponseBody = */ (@Language("json") final String responseBody) -> awsAppConfigParser.parse(
                    /* key = */ key,
                    /* value = */ responseBody,
                    /* buildAppConfigValue = */ booleanAttributeParser
                )
            );
        } catch (final AppConfigValueParseException e) {
            log.error("Failed to parseFromResponseBody object from AWS AppConfig response. Fall back to default flag value", e);
            return e.asErrorEvaluationResult();
        }
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
    EvaluationValue<Value> getValue(
        @NotNull final String key,
        @NotNull final Value defaultValue
    ) {
        try {
            return getInternal(
                /* key = */ key,
                /* defaultValue = */ defaultValue,
                /* asPrimitive = */ false,
                /* parseFromResponseBody = */ (@Language("json") final String responseBody) -> awsAppConfigParser.parse(
                    /* key = */ key,
                    /* value = */ responseBody,
                    /* buildAppConfigValue = */ objectAttributeParser
                )
            );
        } catch (final AppConfigValueParseException e) {
            log.error("Failed to parseFromResponseBody object from AWS AppConfig response. Fall back to default flag value", e);
            return e.asErrorEvaluationResult();
        }
    }

    /**
     * Get generic-typed feature flag value from AWS AppConfig, and return it as {@link EvaluationValue}
     * <b>NOTE that this will throw when getting feature flag value failed.</b>
     *
     * @param key feature flag key. this is specified by Application Author.
     * @param defaultValue default feature flag value to fall back. this is specified by Application Author.
     * @param parseFromResponseBody build {@link V} (= {@link T}-typed {@link EvaluationValue} from JSON response object
     * @param asPrimitive is the target feature flag type in OpenFeature (={@param T} is primitive
     *                    (boolean / number / string)
     * @return {@link T}-typed {@link EvaluationValue}
     * @param <T> feature flag type (like boolean / number etc.) in OpenFeature spec
     * @throws AppConfigValueParseException if parsing JSON response failed, or the JSON response schema is invalid
     */
    @NotNull
    private <T, V extends AppConfigValue<T>> EvaluationValue<T> getInternal(
        @NotNull final String key,
        @NotNull final T defaultValue,
        final boolean asPrimitive,
        @NotNull final Function<String, V> parseFromResponseBody
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

        // Parse SDK response as boolean feature flag. Exception handling is on top.
        final V flagValue = parseFromResponseBody.apply(
            /* t = */ responseBody
        );
        log.info("Flag value [{}: {}] fetched.", key, flagValue.getValue());

        return appConfigValueConverter.toEvaluationValue(
            /* defaultValue = */ defaultValue,
            /* appConfigValue = */ flagValue,
            // true because this is boolean flag
            /* asPrimitive = */ asPrimitive
        );
    }

    /**
     * @param key feature flag key in OpenFeature world
     * @param response SDK response object
     * @return JSON string when repose is valid, otherwise null
     */
    @Nullable
    private String extractResponseBody(
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
