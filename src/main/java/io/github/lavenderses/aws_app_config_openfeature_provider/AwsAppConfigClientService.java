package io.github.lavenderses.aws_app_config_openfeature_provider;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ErrorEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue;

import java.util.concurrent.atomic.AtomicReference;

import io.github.lavenderses.aws_app_config_openfeature_provider.utils.AwsAppConfigClientBuilder;
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;

/**
 * Service layer class for AWS AppConfig.
 */
final class AwsAppConfigClientService {

    private static final Logger log = LoggerFactory.getLogger(AwsAppConfigClientService.class);

    @NotNull
    private final AppConfigDataClient client;

    @NotNull
    private final AwsAppConfigClientOptions options;

    @NotNull
    private final ObjectMapper objectMapper;

    @NotNull
    private final AtomicReference<AwsAppConfigState> appConfigState = new AtomicReference<>(AwsAppConfigState.NONE);

    /**
     * This is just for mockito JUnit extension.
     * Locate this constructor on top on constructors so that mockito can mock all fields in
     * {@link AwsAppConfigClientService}.
     */
    @VisibleForTesting
    AwsAppConfigClientService(
        @NotNull final AppConfigDataClient client,
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final ObjectMapper objectMapper
    ) {
        this.client = client;
        this.options = options;
        this.objectMapper = objectMapper;
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
        objectMapper = ObjectMapperBuilder.build();
    }

    @NotNull
    AwsAppConfigState state() {
        return appConfigState.get();
    }

    /**
     * Get Boolean type flag from AppConfig by {@param key}. Returns
     *
     * <ul>
     *   <li>{@link ErrorCode#FLAG_NOT_FOUND} when flag value related to {@param key} does not exist.
     *   <li>{@link ErrorCode#PARSE_ERROR} when flag value related to {@param key} is not bool value.
     * </ul>
     */
    @NotNull
    EvaluationValue<Boolean> getBoolean(@NotNull final String key) {
        final var request = GetLatestConfigurationRequest.builder()
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

        return evaluateInternal(
            /* key = */ key,
            /* response = */ response,
            /* clazz = */ AppConfigBooleanValue.class
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

    @NotNull
    @VisibleForTesting
    <FLAG, FLAG_VALUE_TYPE extends AppConfigValue<FLAG>> EvaluationValue<FLAG> evaluateInternal(
        @NotNull final String key,
        @NotNull final GetLatestConfigurationResponse response,
        @NotNull final Class<FLAG_VALUE_TYPE> clazz
    ) {
      if (response.configuration().asByteArray().length == 0) {
          log.info("Flag value from AppConfig with key {} does not found.", key);

          return new ErrorEvaluationValue<>(
              /* errorCode = */ ErrorCode.PARSE_ERROR,
              /* errorMessage = */ null,
              /* reason = */ Reason.ERROR);
      }

      final AppConfigValue<FLAG> flagValue = objectMapper.convertValue(response.configuration().asUtf8String(), clazz);
      log.info("Flag value [{}: {}] fetched.", key, flagValue);

      return flagValue.successEvaluationValue();
    }
}
