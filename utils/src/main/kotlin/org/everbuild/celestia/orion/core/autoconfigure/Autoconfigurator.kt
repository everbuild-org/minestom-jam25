package org.everbuild.celestia.orion.core.autoconfigure

import org.slf4j.LoggerFactory

private fun createClusterEnvironmentConfiguration(): EnvironmentConfiguration? {
    if (System.getenv("KUBERNETES_SERVICE_HOST") == null) return null
    return EnvironmentConfiguration(
        mariadbHost = "orionDb",
        mariadbPort = 3306,
        mariadbDatabase = "orion",
        mariadbUsername = "orion",
        mariadbPassword = "orion"
    )
}

private fun createDefaultLocalEnvironmentConfiguration(): EnvironmentConfiguration {
    val logger = LoggerFactory.getLogger(EnvironmentConfiguration::class.java)
    logger.warn("Using local (non-production) environment configuration")
    return EnvironmentConfiguration(
        mariadbHost = "localhost",
        mariadbPort = 3306,
        mariadbDatabase = "orion",
        mariadbUsername = "dev_user",
        mariadbPassword = "development"
    )
}

fun loadDefaultEnvironment(): EnvironmentConfiguration =
    createClusterEnvironmentConfiguration() ?: createDefaultLocalEnvironmentConfiguration()