package io.github.lavenderses.aws_app_config_openfeature_provider.parser;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;

import java.util.function.BiFunction;

/**
 * Parser interface for {@link T}-typed feature flag value from AWS AppConfig JSON response.<br/>
 * <b>NOTE that this parser will throw {@link AppConfigValueParseException} when the JSON response schema is
 * invalid.</b>
 * This is a non-check Exception, caller should handle this exception.
 *
 * @param <T> feature flag type in OpenFeature requirements, such as boolean
 * @param <V> {@link T}-typed {@link AppConfigValue}. this will be returned.
 */
interface AttributeParser<T, V extends AppConfigValue<T>> extends BiFunction<JsonNode, JsonNode, V> {}
