package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;

import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

/**
 * Representation of client response from AWS AppConfig SDK.<br/>
 * Client response schema is following JSON format.
 * (doc: <a href="https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-creating-configuration-and-profile-feature-flags.html">
 *     https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-creating-configuration-and-profile-feature-flags.html</a>)
 * <pre>
 * {@code
 * {
 *   "{key name}": {
 *     // "Add new flag" > "Create flag" > "Off" or "On"
 *     "enabled": boolean,
 *     // "Add new flag" > "Create flag" > "Attribute (optional)"
 *     "{other attribute key 1}": number | string | boolean | regex,
 *     ...
 *   }
 * }
 * }
 * </pre>
 *
 * So, this Provider implementation expects following schema.
 * {@code flag_value} is the fixed key name (THIS IS the schema in this implementation). The value mapped to this key
 * will be used when {@code enable} value
 * is {@code true}.
 * See also in {@link AppConfigValueKey}.
 * <pre>
 * {@code
 * {
 *   "{key name}": {
 *     "enabled": boolean,
 *     "flag_value": number | string | boolean | regex
 *   }
 * }
 * }
 * </pre>
 *
 * See also {@link EvaluationValue} for the overall relationship.
 */
@Getter
@ToString
@EqualsAndHashCode(exclude = "jsonFormat")
@AllArgsConstructor
public abstract class AppConfigValue<T> {

    /**
     * Represents that feature flags in AWS AppConfig is "Off" status.
     * To enable this, toggle "Off" button to "On" in AWS console.<br/>
     * see also. <a href="https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-creating-configuration-and-profile-feature-flags.html#:~:text=Specify%20whether%20the%20feature%20flag%20is%20Enabled%20or%20Disabled%20using%20the%20toggle%20button.">
     *     Documentation</a>.
     * <pre>
     * {@summary
     * > Specify whether the feature flag is Enabled or Disabled using the toggle button.
     * }
     * </pre>
     */
    @NotNull @NonNull protected final Boolean enabled;

    @NotNull @NonNull protected final T value;

    /**
     * JSON schema response from AWS AppConfig. This field is just for error message.
     */
    @Language("json")
    @NotNull
    @NonNull
    private final String jsonFormat;
}
