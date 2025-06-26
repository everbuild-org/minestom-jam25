package org.everbuild.celestia.orion.core.configuration

import java.util.*
import kotlin.reflect.KProperty

class StaticDelegate(private val value: String?) : PropertyDelegate {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Optional<String> = Optional.ofNullable(value)
}