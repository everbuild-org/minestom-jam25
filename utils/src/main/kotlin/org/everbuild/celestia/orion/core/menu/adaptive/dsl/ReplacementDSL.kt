package org.everbuild.celestia.orion.core.menu.adaptive.dsl

import org.everbuild.celestia.orion.core.menu.MenuDSL

class ReplacementDSL(private val replacements: HashMap<String, String>) {
    @MenuDSL
    infix fun String.with(value: String) {
        replacements[this] = value
    }
}