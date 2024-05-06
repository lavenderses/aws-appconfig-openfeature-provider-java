package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ObjectEvaluationValue<T> extends SuccessEvaluationValue<Value> {

    public ObjectEvaluationValue(
        @NotNull T rawValue,
        @NotNull Reason reason
    ) {
        super(
            /* wrappedValue = */ Value.objectToValue(requireNonNull(rawValue, "rawValue")),
            /* reason = */ requireNonNull(reason, "Reason")
        );
    }
}
