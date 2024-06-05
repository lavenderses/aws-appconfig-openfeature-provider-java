package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigIntegerValue;
import org.jetbrains.annotations.NotNull;

/**
 * Parser implementation for int-type feature flag value.
 * This is for int type, but feature flag type will be a number in OpenFeature world.
 */
public final class IntegerAttributeParser
        extends AbstractAttributeParser<Integer, AppConfigIntegerValue> {

    /**
     * Extract "Attribute" as Integer in JSON response from AWS AppConfig.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @Override
    public AppConfigIntegerValue apply(@NotNull JsonNode keyNode) {
        final JsonNode flagValueNode =
                getValidFlagValueNode(
                        /* keyNode= */ keyNode, /* expectedNodeType= */ JsonNodeType.NUMBER);

        return new AppConfigIntegerValue(
                /* enabled= */ enabled(keyNode),
                /* value= */ flagValueNode.asInt(),
                /* responseNode= */ keyNode.toString());
    }
}
