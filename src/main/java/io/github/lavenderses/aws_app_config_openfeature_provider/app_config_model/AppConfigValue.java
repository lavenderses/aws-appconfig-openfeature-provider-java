package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;

import dev.openfeature.sdk.Reason;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.SuccessEvaluationValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public abstract class AppConfigValue<T> {

    protected static final Reason SUCCESS_REASON = Reason.TARGETING_MATCH;

    protected final boolean enable;

    @NonNull
    public abstract SuccessEvaluationValue<T> successEvaluationValue();
}
