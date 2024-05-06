package io.github.lavenderses.aws_app_config_openfeature_provider

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.openfeature.sdk.ErrorCode
import dev.openfeature.sdk.Reason
import dev.openfeature.sdk.Value
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigIntegerValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigObjectValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigStringValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.fixture
import io.github.lavenderses.aws_app_config_openfeature_provider.converter.AppConfigValueConverter
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ErrorEvaluationValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ObjectEvaluationValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.AppConfigValueParseException
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.AwsAppConfigParser
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.BooleanAttributeParser
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.IntegerAttributeParser
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.ObjectAttributeParser
import io.github.lavenderses.aws_app_config_openfeature_provider.parser.StringAttributeParser
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse

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

    @Mock
    private lateinit var booleanAttributeParser: BooleanAttributeParser

    @Mock
    private lateinit var stringAttributeParser: StringAttributeParser

    @Mock
    private lateinit var integerAttributeParser: IntegerAttributeParser

    @Mock
    private lateinit var objectAttributeParser: ObjectAttributeParser

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
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
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
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ booleanAttributeParser,
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

    @Nested
    inner class GetString {

        @Test
        fun normal() {
            // prepare
            val key = "key"
            val defaultValue = "defaultValue"
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val flagValue = AppConfigStringValue::class.fixture()
            val expected = PrimitiveEvaluationValue<String>(
                /* rawValue = */ "test",
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
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ stringAttributeParser,
                )
            doReturn(
                PrimitiveEvaluationValue<String>(
                    /* rawValue = */ "test",
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
                awsAppConfigClientService.getString(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `failed to request to AWS AppConfig`() {
            // prepare
            val key = "key"
            val defaultValue = "defaultValue"
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
                /* errorMessage = */ null,
                /* reason = */ Reason.DEFAULT,
            )

            doThrow(RuntimeException("error"))
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )

            // do
            assertThat(
                awsAppConfigClientService.getString(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(awsAppConfigParser, appConfigValueConfigValueConverter)
        }

        @Test
        fun `response body is null`() {
            // prepare
            val key = "key"
            val defaultValue = "defaultValue"
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn "".toByteArray()
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.PARSE_ERROR,
                /* errorMessage = */ null,
                /* reason = */ Reason.ERROR,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )

            // do
            assertThat(
                awsAppConfigClientService.getString(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(awsAppConfigParser, appConfigValueConfigValueConverter)
        }

        @Test
        fun `failed to call parse`() {
            // prepare
            val key = "key"
            val defaultValue = "defaultValue"
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.PARSE_ERROR,
                /* errorMessage = */ """errorMessage. Response from AWS AppConfig: { "foo": "bar" }""",
                /* reason = */ Reason.ERROR,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )
            doThrow(
                AppConfigValueParseException(
                    /* response = */ """{ "foo": "bar" }""",
                    /* errorMessage = */ "errorMessage",
                    /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT,
                ),
            )
                .whenever(awsAppConfigParser)
                .parse(
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ stringAttributeParser,
                )

            // do
            assertThat(
                awsAppConfigClientService.getString(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(appConfigValueConfigValueConverter)
        }
    }

    @Nested
    inner class GetInteger {

        @Test
        fun normal() {
            // prepare
            val key = "key"
            val defaultValue = 12345
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val flagValue = AppConfigIntegerValue::class.fixture()
            val expected = PrimitiveEvaluationValue<Int>(
                /* rawValue = */ 12345,
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
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ integerAttributeParser,
                )
            doReturn(
                PrimitiveEvaluationValue<Int>(
                    /* rawValue = */ 12345,
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
                awsAppConfigClientService.getInteger(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `failed to request to AWS AppConfig`() {
            // prepare
            val key = "key"
            val defaultValue = 12345
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
                /* errorMessage = */ null,
                /* reason = */ Reason.DEFAULT,
            )

            doThrow(RuntimeException("error"))
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )

            // do
            assertThat(
                awsAppConfigClientService.getInteger(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(
                awsAppConfigParser,
                appConfigValueConfigValueConverter,
                integerAttributeParser,
            )
        }

        @Test
        fun `response body is null`() {
            // prepare
            val key = "key"
            val defaultValue = 12345
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn "".toByteArray()
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.PARSE_ERROR,
                /* errorMessage = */ null,
                /* reason = */ Reason.ERROR,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )

            // do
            assertThat(
                awsAppConfigClientService.getInteger(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(
                awsAppConfigParser,
                appConfigValueConfigValueConverter,
                integerAttributeParser,
            )
        }

        @Test
        fun `failed to call parse`() {
            // prepare
            val key = "key"
            val defaultValue = 12345
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.PARSE_ERROR,
                /* errorMessage = */ """errorMessage. Response from AWS AppConfig: { "foo": "bar" }""",
                /* reason = */ Reason.ERROR,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )
            doThrow(
                AppConfigValueParseException(
                    /* response = */ """{ "foo": "bar" }""",
                    /* errorMessage = */ "errorMessage",
                    /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT,
                ),
            )
                .whenever(awsAppConfigParser)
                .parse(
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ integerAttributeParser,
                )

            // do
            assertThat(
                awsAppConfigClientService.getInteger(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(appConfigValueConfigValueConverter)
        }
    }

    @Nested
    inner class GetObject {

        @Test
        fun normal() {
            // prepare
            val key = "key"
            val defaultValue = Value(true)
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val flagValue = AppConfigObjectValue::class.fixture()
            val expected = ObjectEvaluationValue<Boolean>(
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
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ objectAttributeParser,
                )
            doReturn(
                ObjectEvaluationValue<Boolean>(
                    /* rawValue = */ true,
                    /* reason = */ Reason.TARGETING_MATCH,
                ),
            )
                .whenever(appConfigValueConfigValueConverter)
                .toEvaluationValue(
                    /* defaultValue = */ defaultValue,
                    /* appConfigValue = */ flagValue,
                    /* asPrimitive = */ false,
                )

            // do & verify
            assertThat(
                awsAppConfigClientService.getValue(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `failed to request to AWS AppConfig`() {
            // prepare
            val key = "key"
            val defaultValue = Value(true)
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.FLAG_NOT_FOUND,
                /* errorMessage = */ null,
                /* reason = */ Reason.DEFAULT,
            )

            doThrow(RuntimeException("error"))
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )

            // do
            assertThat(
                awsAppConfigClientService.getValue(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(awsAppConfigParser, appConfigValueConfigValueConverter)
        }

        @Test
        fun `response body is null`() {
            // prepare
            val key = "key"
            val defaultValue = Value(true)
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn "".toByteArray()
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.PARSE_ERROR,
                /* errorMessage = */ null,
                /* reason = */ Reason.ERROR,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )

            // do
            assertThat(
                awsAppConfigClientService.getValue(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(awsAppConfigParser, appConfigValueConfigValueConverter)
        }

        @Test
        fun `failed to call parse`() {
            // prepare
            val key = "key"
            val defaultValue = Value(true)
            val request = GetLatestConfigurationRequest.builder()
                .configurationToken("token")
                .build()
            val configuration = mock<SdkBytes> {
                on { asByteArray() } doReturn """{ "foo": "bar" }""".toByteArray()
                on { asUtf8String() } doReturn """{ "foo": "bar" }"""
            }
            val response = mock<GetLatestConfigurationResponse> {
                on { configuration() } doReturn configuration
            }
            val expected = ErrorEvaluationValue<Value>(
                /* errorCode = */ ErrorCode.PARSE_ERROR,
                /* errorMessage = */ """errorMessage. Response from AWS AppConfig: { "foo": "bar" }""",
                /* reason = */ Reason.ERROR,
            )

            doReturn(response)
                .whenever(client)
                .getLatestConfiguration(
                    /* getLatestConfigurationRequest = */ request,
                )
            doThrow(
                AppConfigValueParseException(
                    /* response = */ """{ "foo": "bar" }""",
                    /* errorMessage = */ "errorMessage",
                    /* evaluationResult = */ EvaluationResult.INVALID_ATTRIBUTE_FORMAT,
                ),
            )
                .whenever(awsAppConfigParser)
                .parse(
                    /* key = */ key,
                    /* value = */ """{ "foo": "bar" }""",
                    /* buildAppConfigValue = */ objectAttributeParser,
                )

            // do
            assertThat(
                awsAppConfigClientService.getValue(
                    /* key = */ key,
                    /* defaultValue = */ defaultValue,
                ),
            ).isEqualTo(expected)

            // verify
            verifyNoInteractions(appConfigValueConfigValueConverter)
        }
    }
}
