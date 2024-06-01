package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value

import dev.openfeature.sdk.Reason

fun primitiveEvaluationValue() = PrimitiveEvaluationValue<String>(
    /* rawValue = */ "value",
    /* reason = */ Reason.STATIC,
)
