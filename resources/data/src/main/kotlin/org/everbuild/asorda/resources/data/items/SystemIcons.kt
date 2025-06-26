package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.AnimatedTexture
import org.everbuild.asorda.resources.data.api.texture.Texture
import team.unnamed.creative.model.Model

object SystemIcons : ContentList("util") {
    val buttonModel = includeModel("utils/button")
    val oversizeFill = createItem("icon_text") {
        model("utils/icon_text")
    }

    val hourglassTexture = AnimatedTexture("utils/hourglass") {
        frameTime = 5
    }
    val hourglassModel = createModel {
        parent(Model.ITEM_GENERATED)
        textures(
            "layer0" to hourglassTexture
        )
    }
    val hourglass = createItem("hourglass") {
        model(hourglassModel)
    }

    val cubitCoinTexture = Texture("general/cubit_coin")
    val cubitCoinModel = createModel {
        parent(Model.ITEM_GENERATED)
        textures(
            "layer0" to cubitCoinTexture
        )
    }
}