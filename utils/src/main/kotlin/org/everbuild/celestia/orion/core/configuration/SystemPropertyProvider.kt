package org.everbuild.celestia.orion.core.configuration

class SystemPropertyProvider(private val namespace: String) : ConfigurationProvider {
    private fun reformatName(key: String) = "cel.$namespace.$key"

    override fun getDelegation(key: String): PropertyDelegate {
        return StaticDelegate(System.getProperty(reformatName(key)))
    }

    override fun setDefault(key: String, value: String) {
        // not applicable
    }
}