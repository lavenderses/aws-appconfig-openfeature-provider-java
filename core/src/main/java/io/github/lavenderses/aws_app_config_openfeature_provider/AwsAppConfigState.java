package io.github.lavenderses.aws_app_config_openfeature_provider;

import dev.openfeature.sdk.ProviderState;
import org.jetbrains.annotations.NotNull;

public enum AwsAppConfigState {
    NONE(ProviderState.NOT_READY),
    PREPARING(ProviderState.NOT_READY),
    READY(ProviderState.READY),
    SHUTTING_DOWN(ProviderState.STALE),
    SHUT_DOWNED(ProviderState.STALE),
    ERROR(ProviderState.ERROR);

    private final ProviderState providerState;

    AwsAppConfigState(@NotNull final ProviderState providerState) {
        this.providerState = providerState;
    }

    @NotNull
    ProviderState asProviderState() {
        return providerState;
    }
}
