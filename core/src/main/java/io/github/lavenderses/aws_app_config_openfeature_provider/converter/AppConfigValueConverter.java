package io.github.lavenderses.aws_app_config_openfeature_provider.converter;

import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigObjectValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ObjectEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.SuccessEvaluationValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import static java.util.Objects.requireNonNull;

/**
 * Converter between {@link AppConfigValue}, which is the POJO of response from AWS AppConfig, and
 * {@link EvaluationValue}, which is the intermediate representation for {@link FlagEvaluationDetails}.
 */
public final class AppConfigValueConverter {

    /**
     * Converts {@param  appConfigValue} to appropriate typed {@link EvaluationValue}.
     * Once {@link AppConfigValue} is instantiated, and thus (see javadoc), returned {@link EvaluationValue} is
     * implementation of {@link SuccessEvaluationValue}.
     *
     * @param defaultValue a default value Application Author specifies
     * @param <T> target feature flag type (boolean / number / string)
     */
    public <T> EvaluationValue<T> toPrimitiveEvaluationValue(
        @NotNull final T defaultValue,
        @NotNull final AppConfigValue<T> appConfigValue
    ) {
        final ConversionResult<T> conversionResult = decideValue(
            /* defaultValue = */ defaultValue,
            /* appConfigValue = */ appConfigValue
        );

        return new PrimitiveEvaluationValue<>(
            /* rawValue = */ conversionResult.getFeatureFlagValue(),
            /* reason = */ conversionResult.getEvaluationResult().getReason()
        );
    }

    /**
     * Converts {@param  appConfigValue} to object typed EvaluationValue, {@link ObjectEvaluationValue}
     * Once {@link AppConfigObjectValue} is instantiated, and thus (see javadoc), returned {@link ObjectEvaluationValue}
     * is implementation of {@link SuccessEvaluationValue}.
     *
     * @param defaultValue a default value Application Author specifies
     */
    @NotNull
    public ObjectEvaluationValue toObjectEvaluationValue(
        @NotNull final Value defaultValue,
        @NotNull final AppConfigObjectValue appConfigObjectValue
    ) {
        final ConversionResult<Value> conversionResult = decideValue(
            /* defaultValue = */ defaultValue,
            /* appConfigValue = */ appConfigObjectValue
        );

        return new ObjectEvaluationValue(
            /* rawValue = */ conversionResult.getFeatureFlagValue(),
            /* reason = */ conversionResult.getEvaluationResult().getReason()
        );
    }

    @VisibleForTesting
    @NotNull
    <T> ConversionResult<T> decideValue(
        @NotNull final T defaultValue,
        @NotNull final AppConfigValue<T> appConfigValue
    ) {
        final boolean enabled = appConfigValue.getEnabled();

        final T featureFlagValue;
        final EvaluationResult evaluationResult;
        if (enabled) {
            featureFlagValue = requireNonNull(appConfigValue.getValue(), "This is bug. Value must be non-null.");
            evaluationResult = EvaluationResult.SUCCESS;
        } else {
            // if feature flag is disabled, fallback to default value
            featureFlagValue = defaultValue;
            evaluationResult = EvaluationResult.FLAG_DISABLED;
        }

        return ConversionResult.<T>builder()
            .featureFlagValue(featureFlagValue)
            .evaluationResult(evaluationResult)
            .build();
    }
}
