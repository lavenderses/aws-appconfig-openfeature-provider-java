package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import static java.util.Objects.nonNull;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Normative;
import io.github.lavenderses.aws_app_config_openfeature_provider.meta.Requirements;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Error value builder for {@link ProviderEvaluation}.
 */
@Requirements(
        number = "2.2.7",
        kind = Normative.MUST,
        by =
                "Each field in this class."
                        + "This class annotate errorCode and errorMessage as NOT-null. And providerEvaluation method uses them. Thus"
                        + "return value as ProviderEvaluation will contains errorCode and (optional) errorMessage.")
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ErrorEvaluationValue<T> implements EvaluationValue<T> {

    @NotNull @NonNull private final ErrorCode errorCode;

    @Nullable private final String errorMessage;

    @NotNull @NonNull private final Reason reason;

    @NotNull
    @Override
    public Reason reason() {
        return reason;
    }

    @NotNull
    @Override
    public ProviderEvaluation<T> providerEvaluation() {
        final ProviderEvaluation.ProviderEvaluationBuilder<T> builder =
                ProviderEvaluation.builder();

        if (nonNull(errorMessage)) {
            builder.errorMessage(errorMessage);
        }

        return builder.errorCode(errorCode)
                .reason(reason().name())
                .flagMetadata(metadata())
                .build();
    }
}
