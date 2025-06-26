package org.everbuild.celestia.orion.core.menu.adaptive

import org.everbuild.celestia.orion.core.menu.adaptive.token.AdaptiveUIToken
import org.everbuild.celestia.orion.core.menu.adaptive.token.SpaceLayerToken
import org.everbuild.celestia.orion.core.packs.OrionPacks

class AdaptiveUILayer(private val tokens: List<AdaptiveUIToken>) {
    private fun calculateWidth(): Int = tokens.sumOf { it.calculateWidth() } + tokens.size - 1
    fun render(): String =
        tokens.joinToString(SUBTWO_TOKEN) { it.render() } + SpaceLayerToken.segmentWidth(calculateWidth(), -1) + OrionPacks.getCharacterCodepoint("spacing_-16")

    companion object {
        val SUBTWO_TOKEN = OrionPacks.getCharacterCodepoint("spacing_-2")
    }
}