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
        val siliconDust = item(5, 0)
        val oil = item(0, 1)
    }

    private val pipeTexture = Texture("jam/pipe")
    private val vacuumTexture = Texture("jam/vacuum")

    val bioScraps = defaultModelItem("bio_scraps", ItemSprites.bioScraps)
    val cableComponent = defaultModelItem("cable_component", ItemSprites.cableComponent)
    val digitalComponent = defaultModelItem("digital_component", ItemSprites.digitalComponent)
    val metalScraps = defaultModelItem("metal_scraps", ItemSprites.metalScraps)
    val hammer = defaultModelItem("hammer", ItemSprites.hammer)
    val siliconDust = defaultModelItem("silicon", ItemSprites.siliconDust)
    val missile1 = defaultModelItem("missile1", Texture("jam/missile1") )
    val oil = defaultModelItem("oil", ItemSprites.oil )
    val pipeItem = createItem("pipeItem") {
        model(createModel {
            parent(includeModel("jam/pipe_hand"))
            textures(
                "0" to pipeTexture
            )
        })
    }
    val vacuumItem = createItem("vacuumItem") {
        model(createModel {
            parent(includeModel("jam/vacuum"))
            textures(
                "0" to vacuumTexture
            )
        })
    }
    val assemblyArmItem = createItem("assemblyArmItem") {
        model(createModel {
            parent(includeModel("jam/assembly_arm_item"))
            textures(
                "0" to Texture("jam/arm_item")
            )
        })
    }
    val pipeCrafterItem = createItem("pipe_crafter") {
        model(createModel {
            parent(includeModel("jam/pipe_assembler"))
            textures(
                "0" to Texture("jam/pipe_assembler")
            )
        })
    }
    val cableCrafterItem = createItem("cable_crafter") {
        model(createModel {
            parent(includeModel("jam/cable_assembler"))
            textures(
                "0" to Texture("jam/cable_assembler")
            )
        })
    }
    val digitalCrafterItem = createItem("digital_crafter") {
        model(createModel {
            parent(includeModel("jam/digital_assembler"))
            textures(
                "0" to Texture("jam/digital_assembler")
            )
        })
    }
    val storageTankItem = createItem("storage_tank") {
        model(createModel {
            parent(includeModel("jam/storage_tank"))
            textures(
                "0" to Texture("jam/storage_tank")
            )
        })
    }
    val missileCrafterItem = createItem("missile_crafter") {
        model(createModel {
            parent(includeModel("jam/basic_missile_assembler"))
            textures(
                "0" to Texture("jam/basic_missile_assembler")
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