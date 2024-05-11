package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.app

import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.healthcheck.HealthCheckService
import io.github.oshai.kotlinlogging.KotlinLogging

private const val PORT = 8080

private val log = KotlinLogging.logger {}

fun main() {
    Server.builder()
        .http(PORT)
        .annotatedService(FlagService())
        .serviceUnder(
            /* pathPrefix = */ "/internal/l7check",
            /* service = */ HealthCheckService.of(),
        )
        .build()
        .apply {
            closeOnJvmShutdown()
            start().join()
        }
    log.info { "Server has been started. Serving FlagService at http://127.0.0.1:$PORT" }
}
