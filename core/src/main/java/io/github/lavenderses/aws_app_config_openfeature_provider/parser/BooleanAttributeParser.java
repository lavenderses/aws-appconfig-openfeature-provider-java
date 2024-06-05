package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import org.jetbrains.annotations.NotNull;

/**
 * Parser implementation for boolean-type feature flag value.
 */
public final class BooleanAttributeParser
        extends AbstractAttributeParser<Boolean, AppConfigBooleanValue> {

    /**
     * Extract "Attribute" as Boolean in JSON response from AWS AppConfig.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @Override
    public AppConfigBooleanValue apply(@NotNull JsonNode keyNode) {
        final JsonNode flagValueNode =
                getValidFlagValueNode(
                        /* keyNode= */ keyNode, /* expectedNodeType= */ JsonNodeType.BOOLEAN);

        return new AppConfigBooleanValue(
                /* enabled= */ enabled(keyNode),
                /* value= */ flagValueNode.asBoolean(),
                /* responseNode= */ keyNode.toString());
    }
}
