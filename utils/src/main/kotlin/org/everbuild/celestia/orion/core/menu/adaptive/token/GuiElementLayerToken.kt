package org.everbuild.celestia.orion.core.menu.adaptive.token

import org.everbuild.celestia.orion.core.packs.OrionPacks

class GuiElementLayerToken(kind: String) : AdaptiveUIToken {
    private val character = OrionPacks.getCharacterCodepoint(kind)
    private val size = OrionPacks.getCharacterSize(kind)

    override fun calculateWidth(): Int = size.width
    override fun render(): String = character
}