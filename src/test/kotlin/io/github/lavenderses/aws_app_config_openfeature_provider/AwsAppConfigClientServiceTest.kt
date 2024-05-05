package io.github.lavenderses.aws_app_config_openfeature_provider

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.JsonNode
import dev.openfeature.sdk.Reason
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AwsAppConfigParser
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.fixture
import io.github.lavenderses.aws_app_config_openfeature_provider.converter.AppConfigValueConverter
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue
import org.apache.logging.log4j.util.BiConsumer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse
import java.util.function.BiFunction

@ExtendWith(MockitoExtension::class)
class AwsAppConfigClientServiceTest {

    @InjectMocks
    private lateinit var awsAppConfigClientService: AwsAppConfigClientService

    @Mock
    private lateinit var client: AppConfigDataClient

    @Spy
    private val options = AwsAppConfigClientOptions::class.fixture().run {
        toBuilder()
            .applicationName("token")
            .build()
    }

    @Mock
    private lateinit var awsAppConfigParser: AwsAppConfigParser

    @Mock
    private lateinit var appConfigValueConfigValueConverter: AppConfigValueConverter

    @Nested
    inner class GetBoolean {

        @Test
        fun normal() {
            // prepare
            val key = "key"
            val defaultValue = false
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
            val flagValue = AppConfigBooleanValue::class.fixture()
            val expected = PrimitiveEvaluationValue<Boolean>(
                /* rawValue = */ true,
                /* reason = */ Reason.TARGETING_MATCH,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )
            doReturn(flagValue)
                .whenever(awsAppConfigParser)
                .parse(
                    /* key = */ eq(key),
                    /* value = */ eq("abc"),
                    /* buildAppConfigValue = */ any<BiFunction<JsonNode, JsonNode, AppConfigBooleanValue>>(),
                )
            doReturn(
                PrimitiveEvaluationValue<Boolean>(
                    /* rawValue = */ true,
                    /* reason = */ Reason.TARGETING_MATCH,
                ),
            )
                .whenever(appConfigValueConfigValueConverter)
                .toEvaluationValue(
                    /* defaultValue = */ defaultValue,
                    /* appConfigValue = */ flagValue,
                    /* asPrimitive = */ true,
                )

            // do & verify
            assertThat(
                awsAppConfigClientService.getBoolean(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)
        }
    }
}
