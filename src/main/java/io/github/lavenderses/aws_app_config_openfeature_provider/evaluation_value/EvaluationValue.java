package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import dev.openfeature.sdk.ImmutableMetadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Normative;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Requirements;
import org.jetbrains.annotations.NotNull;

/**
 * Interface result class between OpenFeature Provider ({@link PrimitiveEvaluationValue} and AWS AppConfig value
 * ({@link AppConfigValue}). <br>
 * A service implementation which fetches flag value from AWS AppConfig returns this record so that separating
 * OpenFeature spec and AWS AppConfig client interface.<br/>
 * <pre>
 * {@code
 * [Client] * OpenFeature SDK
 *    |
 * [Evaluation API] * OpenFeature SDK
 *    | - ProviderEvaluation<T>
 * [Provider impl]
 *    | - EvaluationValue<T> (this class)
 * [AWS AppConfig client]
 *    | - via AWS SDK
 * [AWS AppConfig instance]
 * }
 * </pre>
 *
 * @param <T> generics for {@link ProviderEvaluation}
 */
public interface EvaluationValue<T> {

    @Requirements(
        number = "2.2.5",
        kind = Normative.SHOULD,
        by = """
            Implementing this interface method.
            This method will be called on each `providerEvaluation` method and the returned value (`Reason`) will be set
            to ProviderEvaluation field.
        """
    )
    @NotNull
    Reason reason();

    @NotNull
    default ImmutableMetadata metadata() {
        return ImmutableMetadata.builder().build();
    }

    @NotNull
    ProviderEvaluation<T> providerEvaluation();
}
