package io.github.lavenderses.aws_app_config_openfeature_provider.integration_test.app

import java.util.Properties

object PropertiesHelper {

    private const val PROPERTIES_FILE_PATH = "/app.properties"

    private val properties = Properties()
        .apply {
            load(object {}.javaClass.getResourceAsStream(PROPERTIES_FILE_PATH))
        }

    /**
     * Get variable from properties or environment.
     *
     */
    fun getProperties(property: AppProperties): String {
        return System.getenv(property.env)
            ?: properties.getProperty(property.properties)
            ?: property.defaultValue
    }
}
