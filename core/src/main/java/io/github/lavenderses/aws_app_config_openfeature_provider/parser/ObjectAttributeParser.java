package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.ImmutableStructure;
import dev.openfeature.sdk.Structure;
import dev.openfeature.sdk.Value;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigObjectValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Parser implementation for Object-type (might be structure type in OpenFeature, or else primitive {@link Value} in
 * SDK) feature flag value.
 */
public final class ObjectAttributeParser
        extends AbstractAttributeParser<Value, AppConfigObjectValue> {

    private final ObjectMapper objectMapper;

    @VisibleForTesting
    ObjectAttributeParser(@NotNull final ObjectMapper objectMapper) {
        this.objectMapper = requireNonNull(objectMapper, "objectMapper");
    }

    public ObjectAttributeParser() {
        objectMapper = ObjectMapperBuilder.build();
    }

    /**
     * Extract "Attribute" as Object in JSON response from AWS AppConfig.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    @Override
    public AppConfigObjectValue apply(@NotNull JsonNode keyNode) {
        // AWS AppConfig returns JSON format string, so this JsonNode is String Type node.
        final JsonNode flagRawValueNode =
                getValidFlagValueNode(/* keyNode= */ keyNode, /* expectedNodeType= */ null);
        if (!flagRawValueNode.isTextual()) {
            throw new AppConfigValueParseException(
                    /* response= */ flagRawValueNode.toString(),
                    /* evaluationResult= */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT);
        }
        final String flagValueJsonString = flagRawValueNode.asText();
        final JsonNode flagValueNode;
        try {
            flagValueNode = objectMapper.readTree(flagValueJsonString);
        } catch (final JsonProcessingException e) {
            throw new AppConfigValueParseException(
                    /* response= */ flagValueJsonString,
                    /* evaluationResult= */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT);
        }

        // add JSON node's value recursively
        final HashMap<String, Value> hashMap = new HashMap<>();
        final Value value =
                convertJsonNodeAsValueRecursively(
                        /* valueNode= */ flagValueNode,
                        /* hashMap= */ hashMap,
                        /* add= */ hashMap::put);

        return new AppConfigObjectValue(
                /* enabled= */ enabled(keyNode),
                /* value= */ value,
                /* responseNode= */ keyNode.toString());
    }

    /**
     * DFS {@param valueNode} recursively, and put its result as {@link Value} related to field key.
     *
     * @param valueNode {@link JsonNode} to DFS
     * @param hashMap temporary hash map to put the result
     * @param add lambda to put to {@param hashMap}
     * @return {@link Value} equals to {@param valueNode}
     */
    @NotNull
    @VisibleForTesting
    Value convertJsonNodeAsValueRecursively(
            @NotNull final JsonNode valueNode,
            @NotNull final Map<String, Value> hashMap,
            @NotNull final BiConsumer<String, Value> add) {
        if (valueNode.isTextual()) {
            final String text = valueNode.asText();

            // If feature flag is datetime, it will be as string type. So, try to parse it as
            // Instant first.
            // When it is not the valid Instant schema, fallback it as string type
            try {
                final Instant instant = Instant.parse(text);
                return new Value(instant);
            } catch (final DateTimeParseException e) {
                return new Value(text);
            }
        } else if (valueNode.isBoolean()) {
            return new Value(valueNode.asBoolean());
        } else if (valueNode.isNumber()) {
            return new Value(valueNode.numberValue().intValue());
        }

        // Search each child node recursively
        valueNode
                .fields()
                .forEachRemaining(
                        entry -> {
                            final String key = entry.getKey();
                            final JsonNode childNode = entry.getValue();

                            if (isNull(childNode)) {
                                return;
                            }
                            final Map<String, Value> childHashMap = new HashMap<>();

                            final Value childValue =
                                    convertJsonNodeAsValueRecursively(
                                            /* valueNode= */ childNode,
                                            /* hashMap= */ childHashMap,
                                            /* add= */ childHashMap::put);

                            hashMap.put(key, childValue);
                        });

        final Structure structure = new ImmutableStructure(hashMap);
        return new Value(structure);
    }
}
