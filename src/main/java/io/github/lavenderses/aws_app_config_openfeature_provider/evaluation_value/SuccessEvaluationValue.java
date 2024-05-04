package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Normative;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Requirements;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;


/**
 * Actual flag value holder class. This class means that flag fetching from AWS AppConfig succeeded
 * including type checks.
 *
 * @param <T> flag type. This is also the generics for {@link ProviderEvaluation}.
 */
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuccessEvaluationValue<T> implements EvaluationValue<T> {

    @Requirements(
        number = "2.2.3",
        kind = Normative.MUST,
        by = """
            Containing this wrapped value as value in resolution details.
            (SuccessEvaluationValue represents 'succeeded normal execution'.)
        """
    )
    private final T wrappedValue;

    private final Reason reason;

    SuccessEvaluationValue(
        @NotNull T wrappedValue,
        @NotNull Reason reason
    ) {
        this.wrappedValue = requireNonNull(wrappedValue, "wrappedValue");
        this.reason = requireNonNull(reason, "reason");
    }

    @NotNull
    @Override
    public Reason reason() {
        return reason;
    }

    @Requirements(
        number = "2.2.6",
        kind = Normative.MUST_NOT
    )
    @NotNull
    @Override
    public ProviderEvaluation<T> providerEvaluation() {
        return ProviderEvaluation.<T>builder()
            .value(wrappedValue)
            .reason(reason().name())
            .flagMetadata(metadata())
            .build();
    }
}
