package io.github.lavenderses.aws_app_config_openfeature_provider.proxy;

import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common interface between AWS AppConfig instance &lt;-&gt; Provider implementation.
 * <br/>
 * <b>The reason this interface is required</b>
 * <br/>
 * Aws AppConfig can be used with multiple ways. These are just samples.
 * <ul>
 *     <li>with AWS AppConfig agent</li>
 *     <li>with direct API to AWS AppConfig</li>
 *     <li>with AWS AppConfig data plane</li>
 * </ul>
 * To hide this difference, this interface is introduced (to connect whatever kind of the AWS AppConfig instance from
 * Provider implementation).
 */
public interface AwsAppConfigProxy extends AutoCloseable {

    /**
     * Get the feature flag value as JSON schema (see {@link AppConfigValue} for the schema).<br/>
     * Note that return value is not validated, this is just raw value from AWS AppConfig.
     *
     * @param key feature flag key
     * @return JSON schema response from AWS AppConfigInstance
     */
    @Language("json")
    @Nullable
    String getRawFlagObject(@NotNull final String key);
}
