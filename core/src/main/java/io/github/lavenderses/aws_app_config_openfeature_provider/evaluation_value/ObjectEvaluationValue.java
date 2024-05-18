package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ObjectEvaluationValue extends SuccessEvaluationValue<Value> {

    public ObjectEvaluationValue(
        @NotNull Value rawValue,
        @NotNull Reason reason
    ) {
        super(
            /* wrappedValue = */ requireNonNull(rawValue, "rawValue"),
            /* reason = */ requireNonNull(reason, "Reason")
        );
    }
}
