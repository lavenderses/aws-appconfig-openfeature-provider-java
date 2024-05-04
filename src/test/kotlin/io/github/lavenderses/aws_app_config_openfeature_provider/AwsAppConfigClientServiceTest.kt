package io.github.lavenderses.aws_app_config_openfeature_provider

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.ObjectMapper
import dev.openfeature.sdk.Reason
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse
import java.util.*

@ExtendWith(MockitoExtension::class)
class AwsAppConfigClientServiceTest {

    @InjectMocks
    private lateinit var awsAppConfigClientService: AwsAppConfigClientService

    @Spy
    private val options = AwsAppConfigClientOptions::class.fixture().run {
        toBuilder()
            .applicationName("token")
            .build()
    }

    @Mock
    private lateinit var client: AppConfigDataClient

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun normal() {
        // prepare
        val key = "key"
        val request = GetLatestConfigurationRequest.builder()
            .configurationToken("token")
            .build()
        val configuration = mock<SdkBytes> {
            on { asByteArray() } doReturn "abc".toByteArray()
            on { asUtf8String() } doReturn "abc"
        }
        val response = mock<GetLatestConfigurationResponse> {
            on { configuration() } doReturn configuration
        }
        val expected = PrimitiveEvaluationValue<Boolean>(
            /* rawValue = */ true,
            /* reason = */ Reason.TARGETING_MATCH,
        )

        doReturn(response)
            .whenever(client)
            .getLatestConfiguration(
                /* getLatestConfigurationRequest = */ request,
            )
        doReturn(
            AppConfigBooleanValue(
                /* enable = */ true,
            ),
        )
            .whenever(objectMapper)
            .convertValue("abc", AppConfigBooleanValue::class.java)

        // do & verify
        assertThat(
            awsAppConfigClientService.getBoolean(
                /* key = */ key,
            ),
        ).isEqualTo(expected)
    }
}
