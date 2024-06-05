package io.github.lavenderses.aws_app_config_openfeature_provider.exception;

import org.jetbrains.annotations.Nullable;

public class ScheduledTaskExecutorCloseException extends AwsAppConfigProviderException {

    public ScheduledTaskExecutorCloseException(@Nullable final String message, @Nullable final Exception e) {
        super(message, e);
    }

    public ScheduledTaskExecutorCloseException(@Nullable final String message) {
        super(message);
    }
}
