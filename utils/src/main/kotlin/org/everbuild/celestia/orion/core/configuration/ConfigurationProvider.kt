package org.everbuild.celestia.orion.core.configuration

interface ConfigurationProvider {
    /**
     * Get the current value
     */
    fun getDelegation(key: String): PropertyDelegate

    /**
     * Set a default value
     */
    fun setDefault(key: String, value: String)
}