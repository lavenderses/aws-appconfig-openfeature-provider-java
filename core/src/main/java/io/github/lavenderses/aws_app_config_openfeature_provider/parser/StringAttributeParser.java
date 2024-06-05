package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigStringValue;
import org.jetbrains.annotations.NotNull;

/**
 * Parser implementation for boolean-type feature flag value.
 */
public final class StringAttributeParser
        extends AbstractAttributeParser<String, AppConfigStringValue> {

    /**
     * Extract "Attribute" as Boolean in JSON response from AWS AppConfig.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @Override
    public AppConfigStringValue apply(@NotNull JsonNode keyNode) {
        final JsonNode flagValueNode =
                getValidFlagValueNode(
                        /* keyNode= */ keyNode, /* expectedNodeType= */ JsonNodeType.STRING);

        return new AppConfigStringValue(
                /* enabled= */ enabled(keyNode),
                /* value= */ flagValueNode.asText(),
                /* responseNode= */ keyNode.toString());
    }
}
