package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValueKey;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Common logic implemented parser interface. These logic are shared to all type of feature flag.
 *
 * @param <T> feature flag type in OpenFeature requirements, such as boolean
 * @param <V> {@link T}-typed {@link AppConfigValue}. this will be returned.
 */
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

    /**
     * Get feature flag value node ({@code "$.{key_name}.flag_value"} in JSON).<br/>
     * e.g. When AWS AppConfig is following (feature flag key name is {@code flag_key}, and feature flag value is
     * number type {@code 12345})...
     * <pre>
     * {@code
     * {
     *     "flag_key": {
     *         "enabled": true,
     *         "flag_value": 12345
     *     }
     * }}
     * </pre>
     * Parameter keyNode is the {@link JsonNode} of following JSON string.
     * <pre>
     * {@code
     * {
     *     "enabled": true,
     *     "flag_value": 12345
     * }
     * }
     * </pre>
     * Return value is the {@link IntNode} with value {@code 123245}.
     *
     * @param expectedNodeType expected `flag_value` node type. if this is null, type check of {@code flag_value} node
     *                         won't be done.
     * @throws AppConfigValueParseException if {@param keyNode} is invalid schema
     */
    @NotNull
    protected final JsonNode getValidFlagValueNode(
        @NotNull final JsonNode keyNode,
        @Nullable final JsonNodeType expectedNodeType
    ) {
        final JsonNode flagValueNode = keyNode.get(AppConfigValueKey.FLAG_VALUE.getKey());

        // If `enabled` is not exists, this is schema error
        if (isNull(flagValueNode) || flagValueNode.isNull()) {
            throw new AppConfigValueParseException(
                /* response = */ keyNode.toString(),
                /* errorMessage = */ "`flag_value` should exist",
                /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT
            );
        }

        // if `flag_value` is not expected type, this is type error
        if (nonNull(expectedNodeType) && flagValueNode.getNodeType() != expectedNodeType) {
            throw new AppConfigValueParseException(
                /* response = */ keyNode.toString(),
                /* errorMessage = */ "`flag_value` value expected to be boolean",
                /* evaluationResult = */ EvaluationResult.ATTRIBUTE_TYPE_MISMATCH
            );
        }

        return flagValueNode;
    }
}
