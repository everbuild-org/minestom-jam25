package org.everbuild.celestia.orion.core.configuration

import java.util.Optional
import kotlin.reflect.KProperty

interface PropertyDelegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Optional<String>
}