package io.github.lavenderses.aws_app_config_openfeature_provider;

import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Hook;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.ProviderState;
import dev.openfeature.sdk.Value;
import java.util.List;

import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Normative;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Requirements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

@Requirements(
    number = {"2.2.1", "2.2.2.1"},
    kind = Normative.MUST,
    by = "Implementing FeatureProvider for each type as final class."
)
public final class AwsAppConfigFeatureProvider implements FeatureProvider {

    @NotNull
    private final AwsAppConfigClientService awsAppConfigClientService;

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

        awsAppConfigClientService = new AwsAppConfigClientService(
            /* options = */ options
        );
    }

    @Override
    public void shutdown() {
        FeatureProvider.super.shutdown();
    }

    @NotNull
    @Override
    public ProviderState getState() {
        return awsAppConfigClientService.state().asProviderState();
    }

    @NotNull
    @Override
    public Metadata getMetadata() {
        // TODO
        return new Metadata() {
            @Override
            public String getName() {
                return "";
            }
        };
    }

    @NotNull
    @Override
    public List<Hook> getProviderHooks() {
        return FeatureProvider.super.getProviderHooks();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(
        @Nullable final String key,
        @Nullable final Boolean defaultValue,
        @Nullable final EvaluationContext evaluationContext
    ) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Boolean> evaluationValue = awsAppConfigClientService.getBoolean(key);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<String> getStringEvaluation(
        @Nullable final String key,
        @Nullable final String defaultValue,
        @Nullable final EvaluationContext evaluationContext
    ) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<String> evaluationValue = awsAppConfigClientService.getString(key);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(
        @Nullable final String key,
        @Nullable final Integer defaultValue,
        @Nullable final EvaluationContext evaluationContext
    ) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Integer> evaluationValue = awsAppConfigClientService.getInteger(key);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(
        @Nullable final String key,
        @Nullable final Double defaultValue,
        @Nullable final EvaluationContext evaluationContext
    ) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Double> evaluationValue = awsAppConfigClientService.getDouble(key);

        return evaluationValue.providerEvaluation();
    }

    @NotNull
    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(
        @Nullable final String key,
        @Nullable final Value defaultValue,
        @Nullable final EvaluationContext evaluationContext
    ) {
        requireNonNull(key, "key");
        requireNonNull(defaultValue, "defaultValue");

        // Get boolean value from AppConfig by key
        final EvaluationValue<Value> evaluationValue = awsAppConfigClientService.getValue(key);

        return evaluationValue.providerEvaluation();
    }
}
