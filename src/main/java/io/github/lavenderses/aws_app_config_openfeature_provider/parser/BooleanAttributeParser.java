package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValueKey;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.isNull;

/**
 * Parser implementation for boolean-type feature flag value.
 */
public final class BooleanAttributeParser extends AbstractAttributeParser<Boolean, AppConfigBooleanValue> {

    /**
     * Extract "Attribute" as Boolean in JSON response from AWS AppConfig.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @Override
    public AppConfigBooleanValue apply(
        @NotNull JsonNode responseNode,
        @NotNull JsonNode keyNode
    ) {
        final JsonNode flagValueNode = keyNode.get(AppConfigValueKey.FLAG_VALUE.getKey());
        if (isNull(flagValueNode) || flagValueNode.isNull()) {
            throw new AppConfigValueParseException(
                /* response = */ keyNode.toString(),
                /* errorMessage = */ "`flag_value` should exist",
                /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT
            );
        }

        if (flagValueNode.getNodeType() == JsonNodeType.BOOLEAN) {
            return new AppConfigBooleanValue(
                /* enabled = */ enabled(keyNode),
                /* value = */ flagValueNode.asBoolean(),
                /* responseNode = */ responseNode.toString()
            );
        } else {
            throw new AppConfigValueParseException(
                /* response = */ keyNode.toString(),
                /* errorMessage = */ "`flag_value` value expected to be boolean",
                /* evaluationResult = */ EvaluationResult.ATTRIBUTE_TYPE_MISMATCH
            );
        }
    }
}
