package io.github.lavenderses.aws_app_config_openfeature_provider.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.openfeature.sdk.Reason
import dev.openfeature.sdk.Value
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigObjectValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.fixture
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.EvaluationResult
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.ObjectEvaluationValue
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AppConfigValueConverterTest {

    @InjectMocks
    private lateinit var appConfigValueConverter: AppConfigValueConverter

    @Test
    fun toPrimitiveEvaluationValue() {
        // prepare
        val enable = true
        val defaultValue = false
        val value = true
        val appConfigBooleanValue = AppConfigBooleanValue::class.fixture(
            enabled = enable,
            value = value,
        )
        val expected = PrimitiveEvaluationValue<Boolean>(
            /* rawValue = */ value,
            /* reason = */ Reason.TARGETING_MATCH,
        )

        // do & verify
        assertThat(
            appConfigValueConverter.toPrimitiveEvaluationValue(
                /* defaultValue = */ defaultValue,
                /* appConfigValue = */ appConfigBooleanValue,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun toObjectEvaluationValue() {
        // prepare
        val enable = true
        val defaultValue = Value(0)
        val value = Value(12345)
        val appConfigObjectValue = AppConfigObjectValue::class.fixture(
            enabled = enable,
            value = value,
        )
        val expected = ObjectEvaluationValue(
            /* rawValue = */ Value(12345),
            /* reason = */ Reason.TARGETING_MATCH,
        )

        // do & verify
        assertThat(
            appConfigValueConverter.toObjectEvaluationValue(
                /* defaultValue = */ defaultValue,
                /* appConfigObjectValue = */ appConfigObjectValue,
            ),
        ).isEqualTo(expected)
    }

    @Test
    fun `decideValue flag disabled`() {
        // prepare
        val enable = false
        val defaultValue = false
        val value = true
        val appConfigBooleanValue = AppConfigBooleanValue::class.fixture(
            enabled = enable,
            value = value,
        )
        val expected = ConversionResult<Boolean>(
            /* featureFlagValue = */ defaultValue,
            /* evaluationResult = */ EvaluationResult.FLAG_DISABLED,
        )

        // do & verify
        assertThat(
            appConfigValueConverter.decideValue(
                /* defaultValue = */ defaultValue,
                /* appConfigValue = */ appConfigBooleanValue,
            ),
        ).isEqualTo(expected)
    }
}
