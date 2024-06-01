package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions
import io.github.lavenderses.aws_app_config_openfeature_provider.model.Credential
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigDataClientProxyConfig
import io.github.lavenderses.aws_app_config_openfeature_provider.task.ScheduledTaskExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionResponse
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class CachedFeatureFlagManagerTest {

    @InjectMocks
    private lateinit var cachedFeatureFlagManager: CachedFeatureFlagManager

    @Spy
    private val awsAppConfigClientOptions = AwsAppConfigClientOptions.builder()
        .applicationName("app")
        .environmentName("env")
        .profile("profile")
        .awsAppConfigProxyConfig(
            AwsAppConfigDataClientProxyConfig.builder()
                .region(Region.AP_NORTHEAST_1)
                .pollingDelay(Duration.ZERO)
                .build(),
        )
        .build()

    @Mock
    private lateinit var appConfigDataClient: AppConfigDataClient

    @Mock
    private lateinit var scheduledTaskExecutor: ScheduledTaskExecutor

    @Test
    fun initSession() {
        // prepare
        val request = StartConfigurationSessionRequest.builder()
            .applicationIdentifier("app")
            .environmentIdentifier("env")
            .configurationProfileIdentifier("profile")
            .build()
        val token = "token"
        val response = mock<StartConfigurationSessionResponse> {
            on { initialConfigurationToken() } doReturn token
        }
        val expected = FeatureFlagCache.builder()
            .token(
                Credential.builder()
                    .rawValue(token)
                    .build(),
            )
            .flags(emptyMap())
            .build()

        doReturn(response)
            .whenever(appConfigDataClient)
            .startConfigurationSession(
                /* startConfigurationSessionRequest = */ request,
            )

        // do
        cachedFeatureFlagManager.initSession()

        // verify
        val responseCache = cachedFeatureFlagManager.responseCache
        assertThat(responseCache.get()).isEqualTo(expected)
    }
}
