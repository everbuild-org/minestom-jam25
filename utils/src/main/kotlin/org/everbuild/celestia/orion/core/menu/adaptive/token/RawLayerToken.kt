package org.everbuild.celestia.orion.core.menu.adaptive.token

import org.everbuild.celestia.orion.core.packs.GlyphSizes
import org.everbuild.celestia.orion.core.packs.OrionPacks

class RawLayerToken(private val text: String) : AdaptiveUIToken {
    override fun calculateWidth(): Int = getTextWidth(text)
    override fun render(): String = text

    companion object {
        fun getTextWidth(text: String): Int = text.split("")
            .filterNot { it.isEmpty() }
            .sumOf { OrionPacks.getCharacterWidth(it[0]) }

        fun getNativeWidth(text: String): Int = text.split("").filterNot { it.isEmpty() }.sumOf { GlyphSizes.findOrNull(it[0]) ?: 8 }
    }
}