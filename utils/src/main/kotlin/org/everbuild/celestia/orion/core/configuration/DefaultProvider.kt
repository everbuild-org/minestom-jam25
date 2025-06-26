package org.everbuild.celestia.orion.core.configuration

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KProperty

class DefaultProvider : ConfigurationProvider {
    private val defaults = hashMapOf<String, String>()

    override fun setDefault(key: String, value: String) {
        defaults[key] = value
    }

    override fun getDelegation(key: String): PropertyDelegate = DefaultDelegation(defaults, key)

    class DefaultDelegation(private val defaults: HashMap<String, String>, private val key: String) : PropertyDelegate {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Optional<String> {
            return Optional.ofNullable(defaults[key])
        }
    }
}