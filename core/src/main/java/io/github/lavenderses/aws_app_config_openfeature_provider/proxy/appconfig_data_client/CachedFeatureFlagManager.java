package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions;
import io.github.lavenderses.aws_app_config_openfeature_provider.model.Credential;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigDataClientProxyConfig;
import io.github.lavenderses.aws_app_config_openfeature_provider.task.ScheduledTask;
import io.github.lavenderses.aws_app_config_openfeature_provider.task.ScheduledTaskExecutor;
import io.github.lavenderses.aws_app_config_openfeature_provider.task.ScheduledTaskOption;
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClientBuilder;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest;
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * Management class of on-memory-cached feature flag.
 * Once this class is instantiated, this starts infinite scheduled task which updates feature flag cache and access
 * token for AWS AppConfig server.
 */
final class CachedFeatureFlagManager implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(AwsAppConfigDataClientProxy.class);

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperBuilder.build();

    /**
     * A task to be executed infinitely to keep session and update token for AWS AppConfig.
     */
    @VisibleForTesting
    final class CachedFeatureFlagUpdateTask implements ScheduledTask {

        @Override
        public void run() {
            // TODO: error handling
            log.info("Start updating AWS AppConfig cache");
            final FeatureFlagCache oldCache = responseCache.get();
            final FeatureFlagCache newCache;

            try {
                // get latest feature flag from AWS AppConfig
                final GetLatestConfigurationResponse response = getLatestConfigurationResponse(
                    /* credential = */ oldCache.getToken()
                );

                // update token and feature flag
                newCache = newCache(
                    /* oldCache = */ oldCache,
                    /* response = */ response
                );
            } catch (final Exception e) {
                log.error("Failed to update feature flag cache", e);
                return;
            }

            if (nonNull(newCache)) {
                responseCache.set(newCache);
                log.info("Updated AWS AppConfig cache: {}", newCache.getFlags());
            }
        }

        @VisibleForTesting
        GetLatestConfigurationResponse getLatestConfigurationResponse(
            @NotNull final Credential token
        ) {
            final GetLatestConfigurationRequest request = GetLatestConfigurationRequest.builder()
                .configurationToken(token.getRawValue())
                .build();

            return client.getLatestConfiguration(
                /* startConfigurationSessionRequest = */ request
            );
        }

        /**
         * Extracts new value to be cached ({@link FeatureFlagCache} from response from AWS AppConfig.
         */
        @Nullable
        @VisibleForTesting
        FeatureFlagCache newCache(
            @NotNull final FeatureFlagCache oldCache,
            @NotNull final GetLatestConfigurationResponse response
        ) {
            // Update cache for the next configuration poll
            final Credential pollingToken = Credential.builder()
                .rawValue(response.nextPollConfigurationToken())
                .build();

            final String jsonResponse = response.configuration().asUtf8String();
            if (jsonResponse.isEmpty()) {
                log.info("Response from AWS AppConfig is empty. Flag is latest, so skip updating cache.");
                log.debug("See more for https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/appconfigdata/AppConfigDataClient.html");

                // just update token
                return oldCache.toBuilder()
                    .token(pollingToken)
                    .build();
            }

            final JsonNode value;
            try {
                value = OBJECT_MAPPER.readTree(jsonResponse);
            } catch (final JsonProcessingException e) {
                log.error(
                    "Unexpected response from AWS AppConfig: {}. Skip updating cache.",
                    response.configuration(),
                    e
                );
                // just update token
                return oldCache.toBuilder()
                    .token(pollingToken)
                    .build();
            }

            final Map<String, JsonNode> newFeatureFlags = new HashMap<>();
            for (final Iterator<Map.Entry<String, JsonNode>> it = value.fields(); it.hasNext(); ) {
                final Map.Entry<String, JsonNode> entry = it.next();
                newFeatureFlags.put(entry.getKey(), entry.getValue());
            }

            // update token and feature flag
            return oldCache.toBuilder()
                .token(pollingToken)
                .flags(newFeatureFlags)
                .build();
        }
    }

    @NotNull
    private final AwsAppConfigClientOptions options;

    @NotNull
    private final AppConfigDataClient client;

    private final AtomicReference<FeatureFlagCache> responseCache = new AtomicReference<>(FeatureFlagCache.EMPTY);

    private final ScheduledTaskExecutor scheduledTaskExecutor;

    private final CachedFeatureFlagUpdateTask task = new CachedFeatureFlagUpdateTask();

    /**
     * Whether to start AWS AppConfig session.
     * This will be false if it is a testing phase.
     */
    private final boolean startSession;

    @VisibleForTesting
    CachedFeatureFlagManager(
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final AppConfigDataClient client,
        @NotNull final ScheduledTaskExecutor scheduledTaskExecutor
    ) {
        this.options = requireNonNull(options, "options");
        this.client = requireNonNull(client, "client");
        this.scheduledTaskExecutor = requireNonNull(scheduledTaskExecutor, "scheduledTaskExecutor");
        startSession = false;
    }

    CachedFeatureFlagManager(
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final AwsAppConfigDataClientProxyConfig config,
        final boolean startSession
    ) {
        this.options = requireNonNull(options, "options");
        this.startSession = startSession;

        final AppConfigDataClientBuilder builder = AppConfigDataClient.builder()
            .region(config.getRegion());
        final Consumer<AppConfigDataClientBuilder> configure =config.getConfigure();
        if (nonNull(configure)) {
            configure.accept(builder);
        }
        this.client = builder.build();

        scheduledTaskExecutor = new ScheduledTaskExecutor(
            /* option = */ ScheduledTaskOption.builder()
                .delay(config.getPollingDelay())
                .build()
        );
    }

    /**
     * Initiates session for AWS AppConfig.
     * And also starts infinite task which updates local cached feature flags and access token to AWS AppConfig.
     */
    @PostConstruct
    void startSession() {
        if (!startSession) {
            return;
        }

        initSession();

        // start scheduled task to update token
        scheduledTaskExecutor.start(
            /* task = */ task
        );
    }

    @PreDestroy
    @Override
    public void close() throws Exception {
        scheduledTaskExecutor.close();
        client.close();
    }

    @VisibleForTesting
    void initSession() {
        log.info("Start initializing AWS AppConfig token");

        // TODO: error handling
        final StartConfigurationSessionRequest request = StartConfigurationSessionRequest.builder()
            .applicationIdentifier(options.getApplicationName())
            .environmentIdentifier(options.getEnvironmentName())
            .configurationProfileIdentifier(options.getProfile())
            .build();

        final StartConfigurationSessionResponse response = client.startConfigurationSession(
            /* startConfigurationSessionRequest = */ request
        );
        final String initToken = response.initialConfigurationToken();
        responseCache.getAndUpdate(
            (oldFeatureFlagCache) -> oldFeatureFlagCache
                .toBuilder()
                .token(
                    Credential.builder()
                        .rawValue(initToken)
                        .build()
                )
                .build()
        );

        log.info("Initialized AWS AppConfig token");
    }

    /**
     * Gets feature flag value with key {@param key}.
     * The return value will be null if the feature flag associated with {@param key} does not found.
     */
    @Nullable
    JsonNode getCachedFeatureFlagByKeyFrom(
        @NotNull final String key
    ) {
        requireNonNull(key, "Key");

        final FeatureFlagCache cache = responseCache.get();
        return cache.getFlags().get(key);
    }

    @VisibleForTesting
    AtomicReference<FeatureFlagCache> getResponseCache() {
        return responseCache;
    }
}
