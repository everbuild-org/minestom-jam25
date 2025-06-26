package org.everbuild.celestia.orion.platform.minestom.command.debug

import java.lang.reflect.Method

data class WrappedMethod(
    val name: String,
    val method: Method,
    val instance: Any,
    val params: List<WrappedParameter>,
) {
    override fun toString(): String {
        return "$name " + params.joinToString(" ") { "<$it>" }
    }
}