package io.github.lavenderses.aws_app_config_openfeature_provider.converter;

import dev.openfeature.sdk.FlagEvaluationDetails;
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
     * Once {@link AppConfigValue} is instantiated, and thus (see javadoc), returned {@link EvaluationValue} will be
     * implementation of {@link SuccessEvaluationValue}.
     *
     * @param defaultValue a default value Application Author specifies
     * @param asPrimitive is the target feature flag type in OpenFeature (={@param T} is primitive
     *                    (boolean / number / string)
     * @param <T> target feature flag type (boolean / number / string / object)
     */
    public <T> EvaluationValue<T> toEvaluationValue(
        @NotNull final T defaultValue,
        @NotNull final AppConfigValue<T> appConfigValue,
        final boolean asPrimitive
    ) {
        if (asPrimitive) {
            return primitiveEvaluationValue(
                /* defaultValue = */ defaultValue,
                /* appConfigValue = */ appConfigValue
            );
        } else {
            // TODO object
            return primitiveEvaluationValue(
                /* defaultValue = */ defaultValue,
                /* appConfigValue = */ appConfigValue
            );
        }
    }

    @VisibleForTesting
    <T> PrimitiveEvaluationValue<T> primitiveEvaluationValue(
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

        return new PrimitiveEvaluationValue<>(
            /* rawValue = */ featureFlagValue,
            /* reason = */ evaluationResult.getReason()
        );
    }

    @VisibleForTesting
    <T> ObjectEvaluationValue<T> objectEvaluationValue(
        @NotNull final AppConfigValue<T> appConfigValue
    ) {
        final T value = requireNonNull(appConfigValue.getValue(), "This is bug. Value must be non-null.");
        return new ObjectEvaluationValue<>(
            /* rawValue = */ value,
            /* reason = */ EvaluationResult.SUCCESS.getReason()
        );
    }
}
