package io.github.lavenderses.aws_app_config_openfeature_provider.proxy.appconfig_data_client

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.lavenderses.aws_app_config_openfeature_provider.utils.ObjectMapperBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

class AwsAppConfigDataClientProxyTest {

    companion object {
        private val OBJECT_MAPPER = ObjectMapperBuilder.build()
    }

    private lateinit var awsAppConfigDataClientProxy: AwsAppConfigDataClientProxy

    @Mock
    private lateinit var cachedFeatureFlagManager: CachedFeatureFlagManager

    private lateinit var close: AutoCloseable

    @BeforeEach
    fun setup() {
        close = openMocks(this)

        awsAppConfigDataClientProxy = AwsAppConfigDataClientProxy(
            /* cachedFeatureFlagManager = */ cachedFeatureFlagManager,
        )
    }

    @AfterEach
    fun teardown() {
        close.close()
    }

    @Test
    fun getRawFlagObject() {
        // prepare
        val key = "key"
        val jsonNode = OBJECT_MAPPER.createObjectNode().apply {
            put("foo", "bar")
        }
        val expected = // language=json
            """{"foo":"bar"}"""

        doReturn(jsonNode)
            .whenever(cachedFeatureFlagManager)
            .getCachedFeatureFlagByKeyFrom(
                /* key = */ key,
            )

        // do & verify
        assertThat(
            awsAppConfigDataClientProxy.getRawFlagObject(
                /* key = */ key,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun `getRawFlagObject when key not found`() {
        // prepare
        val key = "key"
        val expected = null

        doReturn(null)
            .whenever(cachedFeatureFlagManager)
            .getCachedFeatureFlagByKeyFrom(
                /* key = */ key,
            )

        // do & verify
        assertThat(
            awsAppConfigDataClientProxy.getRawFlagObject(
                /* key = */ key,
            ),
        ).isEqualTo(expected)
    }
}
