package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.app

import com.linecorp.armeria.common.AggregatedHttpRequest
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.annotation.RequestConverterFunction
import java.lang.reflect.ParameterizedType

class ObjectFlagRequestConverter : RequestConverterFunction {

    override fun convertRequest(
        ctx: ServiceRequestContext,
        request: AggregatedHttpRequest,
        expectedResultType: Class<*>,
        expectedParameterizedResultType: ParameterizedType?,
    ): Any? {
        if (expectedResultType === String::class.java) {
            return request.contentUtf8()
        }

        return RequestConverterFunction.fallthrough()
    }
}
