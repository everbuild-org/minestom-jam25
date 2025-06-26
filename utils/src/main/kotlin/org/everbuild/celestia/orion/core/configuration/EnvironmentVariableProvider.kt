package org.everbuild.celestia.orion.core.configuration

class EnvironmentVariableProvider(private val namespace: String) : ConfigurationProvider {
    private fun reformatName(key: String) = "cel.$namespace.$key".replace(".", "_").uppercase()

    override fun getDelegation(key: String): PropertyDelegate {
        return StaticDelegate(System.getenv(reformatName(key)))
    }

    override fun setDefault(key: String, value: String) {
        // not applicable
    }
}