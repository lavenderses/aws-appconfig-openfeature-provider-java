package io.github.lavenderses.aws_app_config_openfeature_provider.proxy;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Exception which means that some unexpected error happened while getting feature flag from AWS AppConfig.
 * This exception will be handled in AwsAppConfigClientService, and Application Author should not handle this.
 */
@Getter
public final class AwsAppConfigProxyException extends RuntimeException {

    @NotNull
    private final String message;

    @Nullable
    private final Exception exception;

    public AwsAppConfigProxyException(
        @NotNull final String message,
        @NotNull final Exception exception
    ) {
        this.message = requireNonNull(message, "message");
        this.exception = exception;
    }
}
