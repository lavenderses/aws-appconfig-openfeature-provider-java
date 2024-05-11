package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigDoubleValue;
import org.jetbrains.annotations.NotNull;

/**
 * Parser implementation for double-type feature flag value.
 * This is for int type, but feature flag type will be a number in OpenFeature world.
 */
public final class DoubleAttributeParser extends AbstractAttributeParser<Double, AppConfigDoubleValue> {

    /**
     * Extract "Attribute" as Double in JSON response from AWS AppConfig.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @Override
    public AppConfigDoubleValue apply(
        @NotNull JsonNode keyNode
    ) {
        final JsonNode flagValueNode = getValidFlagValueNode(
            /* keyNode = */ keyNode,
            /* expectedNodeType = */ JsonNodeType.NUMBER
        );

        return new AppConfigDoubleValue(
            /* enabled = */ enabled(keyNode),
            /* value = */ flagValueNode.asDouble(),
            /* responseNode = */ keyNode.toString()
        );
    }
}
