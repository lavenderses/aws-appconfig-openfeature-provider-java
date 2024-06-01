package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.agent

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.AwsAppConfigProxyException
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigAgentProxyConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler

@ExtendWith(MockitoExtension::class)
class AwsAppConfigAgentProxyTest {

    @InjectMocks
    private lateinit var awsAppConfigAgentProxy: AwsAppConfigAgentProxy

    @Spy
    private val awsAppConfigAgentProxyConfig = AwsAppConfigAgentProxyConfig.builder()
        .endpoint(URI("http://localhost:2772"))
        .build()

    @Spy
    private val awsAppConfigClientOptions = AwsAppConfigClientOptions.builder()
        .applicationName("app")
        .environmentName("env")
        .profile("profile")
        .awsAppConfigProxyConfig(awsAppConfigAgentProxyConfig)
        .build()

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var handler: BodyHandler<String>

    @Test
    fun getRawFlagObject() {
        // prepare
        val key = "key"
        val response = mock<HttpResponse<String>> {
            on { body() } doReturn "{}"
        }
        val excepted = "{}"

        doReturn(response)
            .whenever(httpClient)
            .send(
                @Suppress("ktlint:standard:max-line-length")
                /* request = */ HttpRequest.newBuilder()
                    .uri(URI("http://localhost:2772/applications/app/environments/env/configurations/profile?flag=key"))
                    .GET()
                    .build(),
                /* responseBodyHandler = */ handler,
            )

        // do & verify
        assertThat(
            awsAppConfigAgentProxy.getRawFlagObject(
                /* key = */ key,
            ),
        ).isEqualTo(excepted)
    }

    @Test
    fun `getRawFlagObject failed to call API`() {
        // prepare
        val key = "key"

        doThrow(IOException("error"))
            .whenever(httpClient)
            .send(
                @Suppress("ktlint:standard:max-line-length")
                /* request = */ HttpRequest.newBuilder()
                    .uri(URI("http://localhost:2772/applications/app/environments/env/configurations/profile?flag=key"))
                    .GET()
                    .build(),
                /* responseBodyHandler = */ handler,
            )

        // do & verify
        val e = assertThrows<AwsAppConfigProxyException> {
            awsAppConfigAgentProxy.getRawFlagObject(
                /* key = */ key,
            )
        }

        @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
        assertThat(e.message).isEqualTo("Failed to call to AWS AppConfig agent: http://localhost:2772/applications/app/environments/env/configurations/profile?flag=key")
    }
}
