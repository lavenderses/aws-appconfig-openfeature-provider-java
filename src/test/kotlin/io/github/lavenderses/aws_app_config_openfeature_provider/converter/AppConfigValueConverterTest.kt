package io.github.lavenderses.aws_app_config_openfeature_provider.converter

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.openfeature.sdk.Reason
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.AppConfigBooleanValue
import io.github.lavenderses.aws_app_config_openfeature_provider.app_config_model.fixture
import io.github.lavenderses.aws_app_config_openfeature_provider.evaluation_value.PrimitiveEvaluationValue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AppConfigValueConverterTest {

    @InjectMocks
    private lateinit var appConfigValueConverter: AppConfigValueConverter

    @Nested
    inner class Primitive {

        @Test
        fun `boolean flag enabled`() {
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
                appConfigValueConverter.primitiveEvaluationValue(
                    /* defaultValue = */ defaultValue,
                    /* appConfigValue = */ appConfigBooleanValue,
                ),
            ).isEqualTo(expected)
        }

        @Test
        fun `boolean flag disabled`() {
            // prepare
            val enable = false
            val defaultValue = false
            val value = true
            val appConfigBooleanValue = AppConfigBooleanValue::class.fixture(
                enabled = enable,
                value = value,
            )
            val expected = PrimitiveEvaluationValue<Boolean>(
                /* rawValue = */ defaultValue,
                /* reason = */ Reason.DISABLED,
            )

            // do & verify
            assertThat(
                appConfigValueConverter.primitiveEvaluationValue(
                    /* defaultValue = */ defaultValue,
                    /* appConfigValue = */ appConfigBooleanValue,
                ),
            ).isEqualTo(expected)
        }
    }
}
