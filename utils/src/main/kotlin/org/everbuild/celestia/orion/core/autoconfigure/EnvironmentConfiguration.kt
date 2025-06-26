package org.everbuild.celestia.orion.core.autoconfigure

data class EnvironmentConfiguration(
    val mariadbHost: String,
    val mariadbPort: Int,
    val mariadbUsername: String,
    val mariadbPassword: String,
    val mariadbDatabase: String
)