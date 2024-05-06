package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;

import dev.openfeature.sdk.Value;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Boolean type in AWS AppConfig's Attribute.<br/>
 * This feature flag will be mapped as boolean in OpenFeature requirements.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class AppConfigObjectValue extends AppConfigValue<Value> {

    public AppConfigObjectValue(
        @NotNull Boolean enabled,
        @NotNull Value value,
        @Language("json") @NotNull String jsonFormat
    ) {
        super(
            /* enabled = */ requireNonNull(enabled, "enabled"),
            /* value = */ requireNonNull(value, "value"),
            /* jsonFormat = */ requireNonNull(jsonFormat, "jsonFormat")
        );
    }
}
