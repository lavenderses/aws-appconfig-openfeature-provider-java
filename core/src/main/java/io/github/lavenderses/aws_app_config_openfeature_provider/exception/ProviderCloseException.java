package io.github.lavenderses.aws_app_config_openfeature_provider.exception;

import org.jetbrains.annotations.Nullable;

public final class ProviderCloseException extends AwsAppConfigProviderException {

    public ProviderCloseException(@Nullable final String message, @Nullable final Exception e) {
        super(message, e);
    }

    public ProviderCloseException(@Nullable final String message) {
        super(message);
    }
}
