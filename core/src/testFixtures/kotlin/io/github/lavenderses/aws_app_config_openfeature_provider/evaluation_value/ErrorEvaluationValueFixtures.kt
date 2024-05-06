package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value

import dev.openfeature.sdk.ErrorCode
import dev.openfeature.sdk.Reason

fun <T> errorEvaluationValueFixture() = ErrorEvaluationValue<T>(
    /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
    /* errorMessage = */ "errorMessage",
    /* reason = */ Reason.STATIC,
)
