package org.everbuild.celestia.orion.core.util

import net.kyori.adventure.text.Component

fun String.component() = Component.text(this)

operator fun Component.plus(component: Component) = this.append(component)