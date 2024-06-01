package io.github.lavenderses.aws_app_config_openfeature_provider.proxy

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse

class AbstractAwsAppConfigProxyTest {

    private val abstractAwsAppConfigProxy = object : AbstractAwsAppConfigProxy() {
        override fun close() {
            TODO("Not yet implemented")
        }

        // mocks
        override fun getRawFlagObject(key: String): String? {
            TODO("Not yet implemented")
        }
    }

    @Nested
    inner class ExtractResponseBody {

        @Test
        fun normal() {
            // prepare
            val key = "key"
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn SdkBytes.fromUtf8String("""{}""")
            }
            // language=JSON
            val expected = """{}"""

            // do & verify
            assertThat(
                abstractAwsAppConfigProxy.extractResponseBody(
                    /* key = */ key,
                    /* response = */ response,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `empty response`() {
            // prepare
            val key = "key"
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn SdkBytes.fromUtf8String("""""")
            }
            // language=JSON
            val expected: String? = null

            // do & verify
            assertThat(
                abstractAwsAppConfigProxy.extractResponseBody(
                    /* key = */ key,
                    /* response = */ response,
                ),
            ).isEqualTo(expected)
        }
    }
}
