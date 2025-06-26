package org.everbuild.celestia.orion.core.menu.adaptive.dsl

import org.everbuild.celestia.orion.core.menu.adaptive.AdaptiveUILayer
import org.everbuild.celestia.orion.core.menu.adaptive.token.AdaptiveUIToken
import org.everbuild.celestia.orion.core.menu.adaptive.token.GuiElementLayerToken
import org.everbuild.celestia.orion.core.menu.adaptive.token.RawLayerToken
import org.everbuild.celestia.orion.core.menu.adaptive.token.SpaceLayerToken
import org.everbuild.celestia.orion.core.util.replacementsFrom

class LayerDSL(private val replacements: HashMap<String, String>) {
    private val tokens = mutableListOf<AdaptiveUIToken>()

    fun space(width: Int) {
        tokens += SpaceLayerToken(width)
    }

    fun gui(key: String) {
        tokens += GuiElementLayerToken(key.replacementsFrom(replacements))
    }

    fun raw(text: String) {
        tokens += RawLayerToken(text)
    }

    fun replaced(text: String) {
        tokens += RawLayerToken(text.replacementsFrom(replacements))
    }

    operator fun plusAssign(token: AdaptiveUIToken) {
        tokens += token
    }

    private fun build(): AdaptiveUILayer {
        return AdaptiveUILayer(tokens)
    }

    companion object {
        fun dsl(replacements: HashMap<String, String>, block: LayerDSL.() -> Unit) =
            LayerDSL(replacements).apply(block).build()
    }
}