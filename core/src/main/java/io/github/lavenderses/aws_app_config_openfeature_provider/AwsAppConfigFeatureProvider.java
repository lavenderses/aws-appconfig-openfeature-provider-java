package io.github.lavenderses.aws_app_config_openfeature_provider;

import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Hook;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.ProviderState;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.exception.ProviderCloseException;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Normative;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Requirements;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

@Requirements(
        number = {"2.2.1", "2.2.2.1"},
        kind = Normative.MUST,
        by = "Implementing FeatureProvider for each type as final class.")
public final class AwsAppConfigFeatureProvider implements FeatureProvider {

    private static final String META_NAME = "AwsAppConfigFeatureProvider";

    @NotNull private final AwsAppConfigClientService awsAppConfigClientService;

    /**
     * This is just for mockito JUnit extension. Locate this constructor on top on constructors so
     * that mockito can mock all fields in {@link AwsAppConfigFeatureProvider}.
     */
    @VisibleForTesting
    AwsAppConfigFeatureProvider(@NotNull final AwsAppConfigClientService service) {
        this.awsAppConfigClientService = requireNonNull(service, "awsAppConfigClientService");
    }

    /**
     * @param options All client options for {@link AppConfigDataClient}
     */
    public AwsAppConfigFeatureProvider(@Nullable final AwsAppConfigClientOptions options) {
        requireNonNull(options, "AwsAppConfigClientOptions");

        awsAppConfigClientService = new AwsAppConfigClientService(/* options= */ options);
    }

    @Override
    public void initialize(EvaluationContext evaluationContext) throws Exception {
        FeatureProvider.super.initialize(evaluationContext);

        awsAppConfigClientService.initialize();
    }

    @Override
    public void shutdown() {
        try {
            awsAppConfigClientService.close();
        } catch (final Exception e) {
            throw new ProviderCloseException("failed to close client service", e);
        }

        FeatureProvider.super.shutdown();
    }

    @NotNull
    @Override
    public ProviderState getState() {
        return awsAppConfigClientService.state().asProviderState();
    }

    @Requirements(number = "2.2.1", kind = Normative.MUST, by = META_NAME)
    @NotNull
    @Override
    public Metadata getMetadata() {
        return () -> META_NAME;
    }

    @NotNull
    @Override
    public List<Hook> getProviderHooks() {
        return FeatureProvider.super.getProviderHooks();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(
            @NotNull final String key,
            @NotNull final Boolean defaultValue,
            @NotNull final EvaluationContext evaluationContext) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Boolean> evaluationValue =
                awsAppConfigClientService.getBoolean(
                        /* key= */ key, /* defaultValue= */ defaultValue);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<String> getStringEvaluation(
            @Nullable final String key,
            @Nullable final String defaultValue,
            @Nullable final EvaluationContext evaluationContext) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<String> evaluationValue =
                awsAppConfigClientService.getString(
                        /* key= */ key, /* defaultValue= */ defaultValue);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(
            @Nullable final String key,
            @Nullable final Integer defaultValue,
            @Nullable final EvaluationContext evaluationContext) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Integer> evaluationValue =
                awsAppConfigClientService.getInteger(
                        /* key= */ key, /* defaultValue= */ defaultValue);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(
            @Nullable final String key,
            @Nullable final Double defaultValue,
            @Nullable final EvaluationContext evaluationContext) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Double> evaluationValue =
                awsAppConfigClientService.getDouble(
                        /* key= */ key, /* defaultValue= */ defaultValue);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(
            @Nullable final String key,
            @Nullable final Value defaultValue,
            @Nullable final EvaluationContext evaluationContext) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Value> evaluationValue =
                awsAppConfigClientService.getValue(
                        /* key= */ key, /* defaultValue= */ defaultValue);

        return evaluationValue.providerEvaluation();
    }
}
