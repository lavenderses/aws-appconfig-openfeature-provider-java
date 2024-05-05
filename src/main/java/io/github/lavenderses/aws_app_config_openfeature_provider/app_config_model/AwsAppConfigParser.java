package io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult;
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.function.BiFunction;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Parsing service between JSON response from AWS AppConfig and {@link AppConfigValue}, which is the POJO of the
 * response.
 * <br/>
 * Since {@code flag_value} in JSON response can be any type supported by OpenFeature spec, it is hard to handle type
 * by ObjectMapper. This is why parse service is implemented by own.
 */
public final class AwsAppConfigParser {

    private final ObjectMapper objectMapper;

    /**
     * This is just for mockito JUnit extension.
     * Locate this constructor on top on constructors so that mockito can mock all fields in
     * {@link AwsAppConfigParser}.
     */
    @VisibleForTesting
    AwsAppConfigParser(@NotNull final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AwsAppConfigParser() {
        this.objectMapper = ObjectMapperBuilder.build();
    }

    /**
     * Convert JSON response from AWS AppConfig to {@link T}-typed {@link AppConfigValue} (= {@link V}}.
     *
     * @param key feature flag key value in {@link AppConfigValueKey} ({@code "feature flag key name in OpenFeature"})
     * @param value JSON response from AWS AppConfig
     * @param buildAppConfigValue build {@link V} from (request JSON body, key object in response body)
     * @return {@link T}-typed feature flag implementation of {@link AppConfigValue}. Once returned value successfully,
     * it is guaranteed that JSON response satisfies ALL spec of this Provider's spec, and success to evaluation feature
     * flag.
     * @param <T> feature flag type (like boolean / number etc.) in OpenFeature spec
     * @throws AppConfigValueParseException if parsing JSON response failed, or the JSON response schema is invalid
     */
    @NotNull
    public <T, V extends AppConfigValue<T>> V parse(
        @NotNull final String key,
        @NotNull final String value,
        @NotNull final BiFunction<JsonNode, JsonNode, V> buildAppConfigValue
    ) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");

        final JsonNode responseNode;
        try {
            responseNode = objectMapper.readTree(value);
        } catch (JsonProcessingException e) {
            throw new AppConfigValueParseException(
                /* response = */ value,
                /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT
            );
        }
        requireNonNull(responseNode, "responseNode");

        // get key node from response
        final JsonNode keyNode = responseNode.get(key);
        if (isNull(keyNode)) {
            throw new AppConfigValueParseException(
                /* response = */ value,
                /* evaluationResult = */ EvaluationResult.FLAG_NOT_FOUND
            );
        }

        return buildAppConfigValue.apply(
            /* t = */ responseNode,
            /* v = */ keyNode
        );
    }

    /**
     * Extract "Attribute" as Boolean in AWS AppConfig from JSON response.
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return {@link AppConfigBooleanValue} if the response schema is valid
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    public AppConfigBooleanValue attributeAsBoolean(
        @NotNull final JsonNode responseNode,
        @NotNull final JsonNode keyNode
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

    /**
     * {@code enabled} should always exist in AWS AppConfig feature flag.
     * If enable is null, it means that feature flags not found (unexpected JSON schema from AWS AppConfig).
     *
     * @param keyNode a response JSON string from AWS AppConfig
     * @return enabled value if {@param keyNode} is valid schema
     * @throws AppConfigValueParseException when {@param keyNode} is invalid schema
     */
    private boolean enabled(@NotNull final JsonNode keyNode) throws AppConfigValueParseException {
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
