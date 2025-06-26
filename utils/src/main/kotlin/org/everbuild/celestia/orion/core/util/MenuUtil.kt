package org.everbuild.celestia.orion.core.util

import net.kyori.adventure.text.Component

fun genUiTitle(height: Int, title: Component): Component {
    val prefix = "§f\uE00F"
    val heightMapping = hashMapOf(
        1 to "\uE202",
        2 to "\uE203",
        3 to "\uE204",
        4 to "\uE205",
        5 to "\uE206",
        6 to "\uE207"
    )
    val postfix = "\uE0A8\uE000"

    return Component.text(prefix + heightMapping[height] + postfix).append(title)
}