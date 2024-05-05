package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;

import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ErrorEvaluationValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AppConfigValueParseException extends RuntimeException {

    @NotNull
    private static String errorMessage(@NotNull final String response) {
        requireNonNull(response, "response");

        return String.format(
            "Invalid JSON schema from AWS AppConfig. Check whether feature flag key exists, " +
                "configuration for AWS AppConfig or AWS AppConfig client options. " +
                "Response from AWS AppConfig: %s",
            response);
    }

    private final String response;

    private final String errorMessage;

    private final EvaluationResult evaluationResult;

    public AppConfigValueParseException(
        @NotNull final String response,
        @NotNull final String errorMessage,
        @NotNull final EvaluationResult evaluationResult
    ) {
        super(
            /* message = */ String.format("%s. Response from AWS AppConfig: %s", errorMessage, response)
        );

        this.response = response;
        this.errorMessage = String.format("%s. Response from AWS AppConfig: %s", errorMessage, response);
        this.evaluationResult = evaluationResult;
    }

    public AppConfigValueParseException(
        @NotNull final String response,
        @NotNull final EvaluationResult evaluationResult
    ) {
        super(
            /* message = */ errorMessage(response)
        );

        this.response = response;
        errorMessage = errorMessage(response);
        this.evaluationResult = evaluationResult;
    }

    public <T> ErrorEvaluationValue<T> asErrorEvaluationResult() {
        return new ErrorEvaluationValue<>(
            /* errorCode = */ evaluationResult.getErrorCode(),
            /* errorMessage = */ errorMessage,
            /* reason = */ evaluationResult.getReason()
        );
    }
}
