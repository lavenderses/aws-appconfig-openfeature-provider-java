package io.github.lavenderses.aws_app_config_openfeature_provider.proxy;

import static java.util.Objects.requireNonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception which means that some unexpected error happened while getting feature flag from AWS AppConfig.
 * This exception will be handled in AwsAppConfigClientService, and Application Author should not handle this.
 */
public final class AwsAppConfigProxyException extends RuntimeException {

    @NotNull private final String message;

    @Nullable private final Exception exception;

    public AwsAppConfigProxyException(
            @NotNull final String message, @NotNull final Exception exception) {
        this.message = requireNonNull(message, "message");
        this.exception = exception;
    }
}
