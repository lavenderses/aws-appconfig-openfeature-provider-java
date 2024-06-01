package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.lavenderses.aws_app_config_openfeature_provider.model.Credential;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@ToString(callSuper = true)
final class FeatureFlagCache {

    static final FeatureFlagCache EMPTY = FeatureFlagCache.builder()
        .token(
            Credential.builder()
                .rawValue("")
                .build()
        )
        .flags(new HashMap<>())
        .build();

    @NotNull
    @NonNull
    private final Credential token;

    @NotNull
    @NonNull
    private final Map<String, JsonNode> flags;
}
