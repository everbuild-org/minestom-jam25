package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.spritesheet.ManualSpriteSheet
import org.everbuild.asorda.resources.data.api.texture.ResourceImageSource
import org.everbuild.asorda.resources.data.api.texture.Texture
import team.unnamed.creative.model.Model

object JamItems : ContentList("jam") {
    object ItemSprites : ManualSpriteSheet(ResourceImageSource("jam/items"), 6, 6) {
        val bioScraps = item(0, 0)
        val cableComponent = item(1, 0)
        val digitalComponent = item(2, 0)
        val metalScraps = item(3, 0)
        val hammer = item(4, 0)
    }

    private val pipeTexture = Texture("jam/pipe")

    val bioScraps = defaultModelItem("bio_scraps", ItemSprites.bioScraps)
    val cableComponent = defaultModelItem("cable_component", ItemSprites.cableComponent)
    val digitalComponent = defaultModelItem("digital_component", ItemSprites.digitalComponent)
    val metalScraps = defaultModelItem("metal_scraps", ItemSprites.metalScraps)
    val hammer = defaultModelItem("hammer", ItemSprites.hammer)
    val missile1 = defaultModelItem("missile1", Texture("jam/missile1") )
    val pipeItem = createItem("pipeItem") {
        model(createModel {
            parent(includeModel("jam/pipe_hand"))
            textures(
                "0" to pipeTexture
            )
        })
    }

    private fun defaultModelItem(descriptor: String, texture: Texture) = createItem(descriptor) {
        model(createModel {
            parent(Model.ITEM_GENERATED)
            textures(
                "layer0" to texture
            )
        })
    }
}