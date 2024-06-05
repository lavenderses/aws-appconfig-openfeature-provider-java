package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.Reason;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PrimitiveEvaluationValue<T> extends SuccessEvaluationValue<T> {

    public PrimitiveEvaluationValue(@NotNull final T rawValue, @NotNull final Reason reason) {
        super(
                /* wrappedValue= */ requireNonNull(rawValue, "rawValue"),
                /* reason= */ requireNonNull(reason, "Reason"));
    }
}
