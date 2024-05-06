package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValueKey;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import static java.util.Objects.isNull;

abstract class AbstractAttributeParser<T, V extends AppConfigValue<T>> implements AttributeParser<T, V> {

    /**
     * {@code enabled} should always exist in AWS AppConfig feature flag.
     * If enable is null, it means that feature flags not found (unexpected JSON schema from AWS AppConfig).
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return enabled value if {@param keyNode} is valid schema
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @VisibleForTesting
    protected final boolean enabled(@NotNull final JsonNode keyNode) {
        final JsonNode enableNode = keyNode.get(AppConfigValueKey.ENABLED.getKey());
        if (isNull(enableNode) || enableNode.isNull()) {
            throw new AppConfigValueParseException(
                /* response = */ keyNode.toString(),
                /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT
            );
        }

        if (enableNode.getNodeType() == JsonNodeType.BOOLEAN) {
            return enableNode.asBoolean();
        } else {
            throw new AppConfigValueParseException(
                /* response = */ keyNode.toString(),
                /* errorMessage = */ "`enabled` value expected to be boolean",
                /* evaluationResult = */ EvaluationResult.ATTRIBUTE_TYPE_MISMATCH
            );
        }
    }
}
