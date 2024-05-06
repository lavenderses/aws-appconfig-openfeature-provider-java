package io.github.lavenderses.aws_app_config_openfeature_provider.meta;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Requirements {

    /**
     * The requirement ID.<br/>
     * c.f. <a href="https://openfeature.dev/specification/sections/providers">Provider | OpenFeature</a>.
     */
    @NotNull
    String[] number();

    /**
     * The normative level for this requirement.<br/>
     * c.f. <a href="https://openfeature.dev/specification/sections/providers">Provider | OpenFeature</a>.
     */
    @NotNull
    Normative kind();

    /**
     * Implementation note that how this requirement is satisfied in the code. This is nothing more than source code
     * comment.<br/>
     * This can be empty string if it is not necessary.
     */
    @NotNull
    String by() default "";
}
