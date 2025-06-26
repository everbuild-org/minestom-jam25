package org.everbuild.celestia.orion.core.menu.adaptive.dsl

import org.everbuild.celestia.orion.core.menu.MenuDSL
import org.everbuild.celestia.orion.core.menu.adaptive.AdaptiveUILayer
import org.everbuild.celestia.orion.core.menu.adaptive.AdaptiveUIType

class AdaptiveUiDSL(private val replacements: HashMap<String, String>) {
    private val layers = mutableListOf<AdaptiveUILayer>()
    private var titleCentered = false
    private var titleOffset = 0

    @MenuDSL
    fun replace(block: ReplacementDSL.() -> Unit) {
        ReplacementDSL(replacements).apply(block)
    }

    @MenuDSL
    fun layer(block: LayerDSL.() -> Unit) {
        layers += LayerDSL.dsl(replacements, block)
    }

    @MenuDSL
    fun layer(layer: AdaptiveUILayer) {
        layers += layer
    }

    @MenuDSL
    fun guiLayer(key: String) {
        layers += LayerDSL.dsl(replacements) {
            space(-14)
            gui(key)
        }
    }

    @MenuDSL
    fun guiSciFiLayer() {
        guiLayer("menu_scifi_{rows}")
    }
    @MenuDSL
    fun guiBlankLayer() {
        guiLayer("menu_blank_{rows}")
    }

    @MenuDSL
    fun centerTitle() {
        titleCentered = true
    }

    @MenuDSL
    fun centerTitle(flag: Boolean) {
        titleCentered = flag
    }

    @MenuDSL
    fun titleOffset(offset: Int) {
        titleOffset = offset
    }

    private fun build(): AdaptiveUIType {
        return AdaptiveUIType(layers, titleCentered, titleOffset)
    }

    companion object {
        fun dsl(rows: Int, block: AdaptiveUiDSL.() -> Unit) =
            AdaptiveUiDSL(hashMapOf("rows" to "$rows")).apply(block).build()
    }
}