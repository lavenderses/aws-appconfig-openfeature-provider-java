package io.github.lavenderses.aws_app_config_openfeature_provider;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigObjectValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.AppConfigValueParseException;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.AwsAppConfigParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.converter.AppConfigValueConverter;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ErrorEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.BooleanAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.DoubleAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.IntegerAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.ObjectAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.StringAttributeParser;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.AwsAppConfigProxy;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.AwsAppConfigProxyBuilder;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

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
    private final AwsAppConfigProxy awsAppConfigProxy;

    @NotNull
    private final AwsAppConfigParser awsAppConfigParser;

    @NotNull
    private final AppConfigValueConverter appConfigValueConverter;

    @NotNull
    private final BooleanAttributeParser booleanAttributeParser;

    @NotNull
    private final StringAttributeParser stringAttributeParser;

    @NotNull
    private final IntegerAttributeParser integerAttributeParser;

    @NotNull
    private final DoubleAttributeParser doubleAttributeParser;

    @NotNull
    private final ObjectAttributeParser objectAttributeParser;

    /**
     * This is just for mockito JUnit extension.
     * Locate this constructor on top on constructors so that mockito can mock all fields in
     * {@link AwsAppConfigClientService}.
     */
    @VisibleForTesting
    AwsAppConfigClientService(
        @NotNull final AwsAppConfigProxy awsAppConfigProxy,
        @NotNull final AwsAppConfigParser awsAppConfigParser,
        @NotNull final AppConfigValueConverter appConfigValueConverter,
        @NotNull final BooleanAttributeParser booleanAttributeParser,
        @NotNull final StringAttributeParser stringAttributeParser,
        @NotNull final IntegerAttributeParser integerAttributeParser,
        @NotNull final DoubleAttributeParser doubleAttributeParser,
        @NotNull final ObjectAttributeParser objectAttributeParser
    ) {
        this.awsAppConfigProxy = requireNonNull(awsAppConfigProxy, "awsAppConfigProxy");
        this.awsAppConfigParser = requireNonNull(awsAppConfigParser, "AwsAppConfigParse");
        this.appConfigValueConverter = requireNonNull(appConfigValueConverter, "appConfigValueConverter");
        this.booleanAttributeParser = requireNonNull(booleanAttributeParser, "booleanAttributeParser");
        this.stringAttributeParser = requireNonNull(stringAttributeParser, "stringAttributeParser");
        this.integerAttributeParser = requireNonNull(integerAttributeParser, "integerAttributeParser");
        this.doubleAttributeParser = requireNonNull(doubleAttributeParser, "doubleAttributeParser");
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

        awsAppConfigProxy = AwsAppConfigProxyBuilder.build(
            /* options = */ options
        );
        awsAppConfigParser = new AwsAppConfigParser();
        appConfigValueConverter = new AppConfigValueConverter();
        booleanAttributeParser = new BooleanAttributeParser();
        stringAttributeParser = new StringAttributeParser();
        integerAttributeParser = new IntegerAttributeParser();
        doubleAttributeParser = new DoubleAttributeParser();
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
    EvaluationValue<String> getString(
        @NotNull final String key,
        @NotNull final String defaultValue
    ) {
        try {
            return getInternal(
                /* key = */ key,
                /* defaultValue = */ defaultValue,
                /* parseFromResponseBody = */ (@Language("json") final String responseBody) -> awsAppConfigParser.parse(
                    /* key = */ key,
                    /* value = */ responseBody,
                    /* buildAppConfigValue = */ stringAttributeParser
                )
            );
        } catch (final AppConfigValueParseException e) {
            log.error("Failed to parseFromResponseBody object from AWS AppConfig response. Fall back to default flag value", e);
            return e.asErrorEvaluationResult();
        }
    }

    @NotNull
    EvaluationValue<Integer> getInteger(
        @NotNull final String key,
        @NotNull final Integer defaultValue
    ) {
        try {
            return getInternal(
                /* key = */ key,
                /* defaultValue = */ defaultValue,
                /* parseFromResponseBody = */ (@Language("json") final String responseBody) -> awsAppConfigParser.parse(
                    /* key = */ key,
                    /* value = */ responseBody,
                    /* buildAppConfigValue = */ integerAttributeParser
                )
            );
        } catch (final AppConfigValueParseException e) {
            log.error("Failed to parseFromResponseBody object from AWS AppConfig response. Fall back to default flag value", e);
            return e.asErrorEvaluationResult();
        }
    }

    @NotNull
    EvaluationValue<Double> getDouble(
        @NotNull final String key,
        @NotNull final Double defaultValue
    ) {
        try {
            return getInternal(
                /* key = */ key,
                /* defaultValue = */ defaultValue,
                /* parseFromResponseBody = */ (@Language("json") final String responseBody) -> awsAppConfigParser.parse(
                    /* key = */ key,
                    /* value = */ responseBody,
                    /* buildAppConfigValue = */ doubleAttributeParser
                )
            );
        } catch (final AppConfigValueParseException e) {
            log.error("Failed to parseFromResponseBody object from AWS AppConfig response. Fall back to default flag value", e);
            return e.asErrorEvaluationResult();
        }
    }

    @NotNull
    EvaluationValue<Value> getValue(
        @NotNull final String key,
        @NotNull final Value defaultValue
    ) {
        try {
            return getObjectInternal(
                /* key = */ key,
                /* defaultValue = */ defaultValue,
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
     * <b>NOTE that this will throw an exception when getting feature flag value failed.</b>
     *
     * @param key feature flag key. this is specified by Application Author.
     * @param defaultValue default feature flag value to fall back. this is specified by Application Author.
     * @param parseFromResponseBody build {@link V} (= {@link T}-typed {@link EvaluationValue}) from JSON response object
     * @return {@link T}-typed {@link EvaluationValue}
     * @param <T> feature flag type (like boolean / number etc.) in OpenFeature spec
     * @throws AppConfigValueParseException if parsing JSON response failed, or the JSON response schema is invalid
     */
    @NotNull
    private <T, V extends AppConfigValue<T>> EvaluationValue<T> getInternal(
        @NotNull final String key,
        @NotNull final T defaultValue,
        @NotNull final Function<String, V> parseFromResponseBody
    ) {
        final String responseBody;
        try {
            responseBody = awsAppConfigProxy.getRawFlagObject(
                /* key = */ key
            );
        } catch (final Exception e) {
            log.error("Failed to get response from AppConfig.", e);

            return new ErrorEvaluationValue<>(
                /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
                /* errorMessage = */ null,
                /* reason = */ Reason.DEFAULT
            );
        }
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

        return appConfigValueConverter.toPrimitiveEvaluationValue(
            /* defaultValue = */ defaultValue,
            /* appConfigValue = */ flagValue
        );
    }

    /**
     * Get object-typed feature flag value from AWS AppConfig, and return it as {@link EvaluationValue}
     * <b>NOTE that this will throw an exception when getting feature flag value failed.</b>
     *
     * @param key feature flag key. this is specified by Application Author.
     * @param defaultValue default feature flag value to fall back. this is specified by Application Author.
     * @param parseFromResponseBody build {@link AppConfigObjectValue} from JSON response object
     * @throws AppConfigValueParseException if parsing JSON response failed, or the JSON response schema is invalid
     */
    @NotNull
    private EvaluationValue<Value> getObjectInternal(
        @NotNull final String key,
        @NotNull final Value defaultValue,
        @NotNull final Function<String, AppConfigObjectValue> parseFromResponseBody
    ) {
        final String responseBody;
        try {
            responseBody = awsAppConfigProxy.getRawFlagObject(
                /* key = */ key
            );
        } catch (final Exception e) {
            log.error("Failed to get response from AppConfig.", e);

            return new ErrorEvaluationValue<>(
                /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
                /* errorMessage = */ null,
                /* reason = */ Reason.DEFAULT
            );
        }
        if (isNull(responseBody)) {
            return parseErrorEvaluationValue(
                /* errorMessage = */ null
            );
        }

        // Parse SDK response as boolean feature flag. Exception handling is on top.
        final AppConfigObjectValue flagValue = parseFromResponseBody.apply(
            /* t = */ responseBody
        );
        log.info("Flag value [{}: {}] fetched.", key, flagValue.getValue().asStructure().asMap());

        return appConfigValueConverter.toObjectEvaluationValue(
            /* defaultValue = */ defaultValue,
            /* appConfigValue = */ flagValue
        );
    }

    private <T> EvaluationValue<T> parseErrorEvaluationValue(@Nullable final String errorMessage) {
        return new ErrorEvaluationValue<>(
            /* errorCode = */ ErrorCode.PARSE_ERROR,
            /* errorMessage = */ errorMessage,
            /* reason = */ Reason.ERROR
        );
    }
}
