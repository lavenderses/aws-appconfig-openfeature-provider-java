package io.github.lavenderses.aws_app_config_openfeature_provider.converter;

import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@Builder(toBuilder = true)
@Data
class ConversionResult<T> {

    @NotNull @NonNull private final T featureFlagValue;

    @NotNull @NonNull private final EvaluationResult evaluationResult;
}
