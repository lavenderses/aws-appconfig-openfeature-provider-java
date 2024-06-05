package io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value;

import static java.util.Objects.requireNonNull;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.Reason;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Mapping class between "{@link Reason} and {@link ErrorCode}", which is in the OpenFeature world, and
 * "evaluation with AWS AppConfig".
 */
@Getter
public enum EvaluationResult {

    /**
     * Represents that feature flag from AWS AppConfig is valid and flag follows valid scheme in this provider
     * implementation spec.
     */
    SUCCESS(Reason.TARGETING_MATCH, null),

    /**
     * Represents that feature flag in AWS AppConfig is disabled.
     */
    FLAG_DISABLED(Reason.DISABLED, null),

    /**
     * Represents that feature flag related to `key` not found in response from AWS AppConfig.
     */
    FLAG_NOT_FOUND(Reason.ERROR, ErrorCode.FLAG_NOT_FOUND),

    /**
     * Represents that feature flag exists in AWS AppConfig, but the flag's attribute does not follow schema in this
     * provider implementation spec.<br/>
     * As a result, feature flag retrieving result will be fail.
     */
    INVALID_ATTRIBUTE_FORMAT(Reason.ERROR, ErrorCode.PARSE_ERROR),

    /**
     * Represents that feature flag exists in AWS AppConfig, and the flag's attribute follow schema in thi provider
     * implementation spec, but type is different.<br/>
     * (e.g. feature flag in the OpenFeature world expects number value, but attribute type in AWS AppConfig is string
     * and unable to parse as number.<br/>
     * As a result, feature flag retrieving result will be fail.
     */
    ATTRIBUTE_TYPE_MISMATCH(Reason.ERROR, ErrorCode.TYPE_MISMATCH),
    ;

    @NotNull private final Reason reason;

    @Nullable private final ErrorCode errorCode;

    EvaluationResult(@NotNull final Reason reason, @Nullable final ErrorCode errorCode) {
        this.reason = requireNonNull(reason, "Reason");
        this.errorCode = errorCode;
    }
}
