package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.test

enum class FlagType(val path: String) {

    STRING("stringFlag"),
    BOOLEAN("booleanFlag"),
    INT("intFlag"),
    DOUBLE("doubleFlag"),
    OBJECT_VALUE("objectFlag"),
}
