package io.github.lavenderses.aws_app_config_openfeature_provider.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class ObjectMapperBuilder {

    @NotNull
    public static ObjectMapper build() {
        return new ObjectMapper();
    }
}
