package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.AwsAppConfigClientOptions
import io.github.lavenderses.aws_app_config_openfeature_provider.model.Credential
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client.CachedFeatureFlagManager.CachedFeatureFlagUpdateTask
import io.github.lavenderses.aws_app_config_openfeature_provider.proxy.config.AwsAppConfigDataClientProxyConfig
import io.github.lavenderses.aws_app_config_openfeature_provider.task.ScheduledTaskExecutor
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class CachedFeatureFlagUpdateTaskTest {

    companion object {
        private val OBJECT_MAPPER = ObjectMapperBuilder.build()
    }

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

    private lateinit var cachedFeatureFlagUpdateTask: CachedFeatureFlagUpdateTask

    @BeforeEach
    fun setup() {
        cachedFeatureFlagUpdateTask = cachedFeatureFlagManager.CachedFeatureFlagUpdateTask()
    }

    @Nested
    inner class NewCache {

        @Test
        fun normal() {
            // prepare
            val oldCache = FeatureFlagCache.builder()
                .token(
                    Credential.builder()
                        .rawValue("token")
                        .build(),
                )
                .flags(emptyMap())
                .build()
            val flag = // language=json
                """
                  {
                    "a": {
                      "enabled": false,
                      "flag_value": 1
                    },
                    "b": {
                      "enabled": true,
                      "flag_value": "abc"
                    }
                  }
                """.trimIndent()
            val sdkBytes = mock<SdkBytes> {
                on { asUtf8String() } doReturn flag
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { nextPollConfigurationToken() } doReturn "newToken"
                on { configuration() } doReturn sdkBytes
            }
            val expected = FeatureFlagCache.builder()
                .token(
                    Credential.builder()
                        .rawValue("newToken")
                        .build(),
                )
                .flags(
                    mapOf(
                        "a" to OBJECT_MAPPER.createObjectNode().apply {
                            put("enabled", false)
                            put("flag_value", 1)
                        },
                        "b" to OBJECT_MAPPER.createObjectNode().apply {
                            put("enabled", true)
                            put("flag_value", "abc")
                        },
                    ),
                )
                .build()

            // do & verify
            assertThat(
                cachedFeatureFlagUpdateTask.newCache(
                    /* oldCache = */ oldCache,
                    /* response = */ response,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `response is empty`() {
            // prepare
            val oldCache = FeatureFlagCache.builder()
                .token(
                    Credential.builder()
                        .rawValue("token")
                        .build(),
                )
                .flags(
                    mapOf(
                        "a" to OBJECT_MAPPER.createObjectNode().apply {
                            put("enabled", false)
                            put("flag_value", 1)
                        },
                    ),
                )
                .build()
            val flag = ""
            val sdkBytes = mock<SdkBytes> {
                on { asUtf8String() } doReturn flag
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { nextPollConfigurationToken() } doReturn "newToken"
                on { configuration() } doReturn sdkBytes
            }
            val expected = FeatureFlagCache.builder()
                .token(
                    // updated
                    Credential.builder()
                        .rawValue("newToken")
                        .build(),
                )
                .flags(
                    // not updated
                    mapOf(
                        "a" to OBJECT_MAPPER.createObjectNode().apply {
                            put("enabled", false)
                            put("flag_value", 1)
                        },
                    ),
                )
                .build()

            // do & verify
            assertThat(
                cachedFeatureFlagUpdateTask.newCache(
                    /* oldCache = */ oldCache,
                    /* response = */ response,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `unparsable response`() {
            // prepare
            val oldCache = FeatureFlagCache.builder()
                .token(
                    Credential.builder()
                        .rawValue("token")
                        .build(),
                )
                .flags(
                    mapOf(
                        "a" to OBJECT_MAPPER.createObjectNode().apply {
                            put("enabled", false)
                            put("flag_value", 1)
                        },
                    ),
                )
                .build()
            val flag = """invalid json"""
            val sdkBytes = mock<SdkBytes> {
                on { asUtf8String() } doReturn flag
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { nextPollConfigurationToken() } doReturn "newToken"
                on { configuration() } doReturn sdkBytes
            }
            val expected = FeatureFlagCache.builder()
                .token(
                    // updated
                    Credential.builder()
                        .rawValue("newToken")
                        .build(),
                )
                .flags(
                    // not updated
                    mapOf(
                        "a" to OBJECT_MAPPER.createObjectNode().apply {
                            put("enabled", false)
                            put("flag_value", 1)
                        },
                    ),
                )
                .build()

            // do & verify
            assertThat(
                cachedFeatureFlagUpdateTask.newCache(
                    /* oldCache = */ oldCache,
                    /* response = */ response,
                ),
            ).isEqualTo(expected)
        }
    }
}
