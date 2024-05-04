package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value

import dev.openfeature.sdk.Reason

fun objectEvaluationValueFixture() = ObjectEvaluationValue(
    /* rawValue = */ TestValue(),
    /* reason = */ Reason.STATIC,
)

data class TestValue(
    val value: Int = 1,
)
