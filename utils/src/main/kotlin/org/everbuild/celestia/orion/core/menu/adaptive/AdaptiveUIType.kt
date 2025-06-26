package org.everbuild.celestia.orion.core.menu.adaptive

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component

class AdaptiveUIType(private val layers: List<AdaptiveUILayer>, private val titleCentered: Boolean, private val titleOffset: Int) {
    private fun componentLength(component: Component): Int {
        var length = 0
        if (component is TextComponent) {
            length += component.content().sumOf { OrionPacks.getCharacterWidth(it) }
        }

        return length + component.children().sumOf { componentLength(it) }
    }

    fun render(title: Component): Component {
        var inner = layers.joinToString("") { it.render() }

        if (titleCentered) {
            val titleLength = componentLength(title)
            val spaceLength = ((TEXT_WIDTH - titleLength) / 2) - 1
            inner += OrionPacks.getCharacterCodepoint("spacing_$spaceLength")
        }

        if (titleOffset != 0) {
            inner += OrionPacks.getCharacterCodepoint("spacing_${titleOffset-1}")
        }

        return inner
            .component()
            .color(NamedTextColor.WHITE)
            .decorations(setOf(TextDecoration.ITALIC), false)
            .append(title)
    }

    companion object {
        const val TEXT_WIDTH = 162
    }
}