package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;

import lombok.Getter;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * JSON key in the AWS AppConfig response.<br/>
 * This is also representing the valid schema. It is following schema. See also {@link EvaluationValue}.
 * <pre>
 * {@code
 * {
 *     "feature flag key name in OpenFeature": {
 *         "enabled": boolean,
 *         "flag_value": "Value of feature flag in OpenFeature"
 *     }
 * }
 * }
 * </pre>
 */
@Getter
public final class AppConfigValueKey {

    /**
     * Representing whether this feature flag ({@code "feature flag key in Openfeature"}) is enabled in AWS AppConfig.
     * This is mandatory field.<br/>
     * <br/>
     * This field exist is in ensure on AWS AppConfig SDK level.<br/>
     * This is the value in {@code `$."feature flag key name in OpenFeature".enabled`}.
     */
    public static final AppConfigValueKey ENABLED = new AppConfigValueKey("enabled");

    /**
     * The actual flag value's key. This is mandatory field.<br/>
     * <br/>
     * This field exist is NOT in ensure on AWS AppConfig SDK level and this is optional as AWS AppConfig, but
     * <a href="https://openfeature.dev/specification/glossary#application-author">Application Author</a>
     * MUST configure this attribute on AWS console. This is the spec of this Provider.<br/>
     * This is the value in {@code `$."feature flag key name in OpenFeature".flag_value`}.
     */
    public static final AppConfigValueKey FLAG_VALUE = new AppConfigValueKey("flag_value");

    private final String key;

    private AppConfigValueKey(@NotNull final String key) {
        this.key = requireNonNull(key, "key");
    }
}
