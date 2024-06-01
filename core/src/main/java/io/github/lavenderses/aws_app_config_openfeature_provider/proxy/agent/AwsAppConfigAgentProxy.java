package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.agent;

import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions;
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigValue;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.AbstractAwsAppConfigProxy;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.AwsAppConfigProxyException;
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigAgentProxyConfig;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.util.Objects.requireNonNull;

/**
 * Provider implementation proxy for accessing AWS AppConfig instance via AWS AppConfig agent.
 * For what is AWS AppConfig anget, see this document. In short, it is like a side-car container.
 * <a href="https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-integration-containers-agent.html"
 * >https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-integration-containers-agent.html</a>
 */
public final class AwsAppConfigAgentProxy extends AbstractAwsAppConfigProxy {

    @NotNull
    private final AwsAppConfigClientOptions options;

    @NotNull
    private final AwsAppConfigAgentProxyConfig config;

    @NotNull
    private final HttpClient httpClient;

    @NotNull
    private final HttpResponse.BodyHandler<String> handler ;

    /**
     * This is just for mockito JUnit extension.
     * Locate this constructor on top on constructors so that mockito can mock all fields in
     * {@link AwsAppConfigAgentProxy}.
     */
    @VisibleForTesting
    AwsAppConfigAgentProxy(
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final AwsAppConfigAgentProxyConfig awsAppConfigProxyConfig,
        @NotNull final HttpClient httpClient,
        @NotNull final HttpResponse.BodyHandler<String> handler
        ) {
        super();

        this.options = requireNonNull(options, "aws");
        config = requireNonNull(awsAppConfigProxyConfig, "awsAppConfigProxyConfig");
        this.httpClient = requireNonNull(httpClient, "httpClient");
        this.handler = requireNonNull(handler, "HttpResponse.BodyHandler<String>");
    }

    public AwsAppConfigAgentProxy(
        @NotNull final AwsAppConfigClientOptions options,
        @NotNull final AwsAppConfigAgentProxyConfig awsAppConfigProxyConfig
    ) {
        super();

        this.options = requireNonNull(options, "aws");
        config = requireNonNull(awsAppConfigProxyConfig, "awsAppConfigProxyConfig");
        httpClient = setupHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
    }

    @Override
    public void close() {
        // nothing to close
    }

    /**
     * Get the feature flag value as JSON schema (see {@link AppConfigValue} for the schema).<br/>
     * Note that return value is not validated, this is just raw value from AWS AppConfig.
     *
     * @param key feature flag key
     * @return JSON schema response from AWS AppConfigInstance
     */
    @Language("json")
    @Nullable
    @Override
    public String getRawFlagObject(@NotNull String key) {
        final URI requestUri;
        try {
            requestUri = new URI(
                /* schema = */ config.getEndpoint().getScheme(),
                /* userInfo = */ config.getEndpoint().getUserInfo(),
                /* host = */ config.getEndpoint().getHost(),
                /* port = */ config.getEndpoint().getPort(),
                /* path = */ String.format(
                    "/applications/%s/environments/%s/configurations/%s",
                    options.getApplicationName(),
                    options.getEnvironmentName(),
                    options.getProfile()
                ),
                /* query = */ String.format(
                    "flag=%s",
                    key
                ),
                /* fragment = */ null
            );
        } catch (final URISyntaxException e) {
            throw new AwsAppConfigProxyException(
                /* message = */ String.format(
                    "Invalid URI from configuration and flag key: config = %s, key = %s",
                    config,
                    key
                ),
                /* exception = */ e
            );
        }

        final HttpRequest request = HttpRequest.newBuilder()
            .uri(requestUri)
            .GET()
            .build();

        final HttpResponse<String> response;
        try {
            response = httpClient.send(
                /* request = */ request,
                /* responseBodyHandler = */ handler
            );
        } catch (final IOException | InterruptedException e) {
            throw new AwsAppConfigProxyException(
                /* message = */ String.format(
                    "Failed to call to AWS AppConfig agent: %s",
                    requestUri
                ),
                /* exception = */ e
            );
        }

        return response.body();
    }

    @NotNull
    private HttpClient setupHttpClient() {
        return HttpClient.newHttpClient();
    }
}
