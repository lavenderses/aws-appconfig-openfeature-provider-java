package io.github.lavenderses.aws_app_config_openfeature_provider.helper

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

object Time {

    const val FIXED_TIME = "2024-04-01T10:00:00.00Z"

    val fixedInstant = Instant.parse(FIXED_TIME)

    val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))
}
