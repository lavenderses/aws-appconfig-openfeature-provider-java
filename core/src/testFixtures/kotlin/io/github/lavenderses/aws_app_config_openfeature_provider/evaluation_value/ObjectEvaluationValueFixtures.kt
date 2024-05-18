package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value

import dev.openfeature.sdk.Reason
import dev.openfeature.sdk.Value

fun objectEvaluationValueFixture() = ObjectEvaluationValue(
    /* rawValue = */ Value(),
    /* reason = */ Reason.STATIC,
)
