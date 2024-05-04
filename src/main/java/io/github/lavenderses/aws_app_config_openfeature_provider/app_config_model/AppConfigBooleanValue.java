package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;


import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.SuccessEvaluationValue;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
public final class AppConfigBooleanValue extends AppConfigValue<Boolean> {

    public AppConfigBooleanValue(final boolean enable) {
        super(
            /* enable = */ enable
        );
    }

    @NotNull
    @Override
    public SuccessEvaluationValue<Boolean> successEvaluationValue() {
        return new PrimitiveEvaluationValue<>(
            /* rawValue = */ isEnable(),
            /* reason = */ SUCCESS_REASON
        );
    }
}
