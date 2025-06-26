package org.everbuild.celestia.orion.core.configuration

import kotlin.reflect.KProperty

class DelegationStack(val delegates: List<PropertyDelegate>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        for (delegate in delegates) {
            val result = delegate.getValue(thisRef, property)
            if (result.isPresent) return result.get()
        }

        return ""
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        throw UnsupportedOperationException("Cannot set read-only config property $value.")
    }
}