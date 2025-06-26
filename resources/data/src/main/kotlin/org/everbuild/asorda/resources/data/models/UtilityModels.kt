package org.everbuild.asorda.resources.data.models

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.model.ModelResource
import org.everbuild.asorda.resources.data.api.spritesheet.ManualSpriteSheet
import org.everbuild.asorda.resources.data.api.texture.ResourceImageSource
import org.everbuild.asorda.resources.data.api.texture.Texture

object UtilityModels : ContentList() {
    private val buttonSprites = object : ManualSpriteSheet(ResourceImageSource("utils/buttons_spritesheet"), 5, 2) {
        val left = item(0, 0)
        val leftActive = item(0, 1)
        val middle = item(1, 0)
        val middleActive = item(1, 1)
        val middleLg = item(2, 0)
        val middleLgActive = item(2, 1)
        val middleRoundLg = item(3, 0)
        val middleRoundLgActive = item(3, 1)
        val right = item(4, 0)
        val rightActive = item(4, 1)
    }
    private val buttonVerticalSprites = object : ManualSpriteSheet(ResourceImageSource("utils/buttons_vertical_spritesheet"), 5, 2) {
        val left = item(0, 0)
        val leftActive = item(0, 1)
        val middle = item(1, 0)
        val middleActive = item(1, 1)
        val middleLg = item(2, 0)
        val middleLgActive = item(2, 1)
        val middleRoundLg = item(3, 0)
        val middleRoundLgActive = item(3, 1)
        val right = item(4, 0)
        val rightActive = item(4, 1)
    }

    val baseButton = includeModel("utils/button")

    private val overflowingIcon = includeModel("utils/overflowing_icon")

    val textIcon = createModel {
        parent(overflowingIcon)
        textures(
            "layer0" to Texture("utils/icon_text")
        )
    }

    val btnLeft = buttonModel(buttonSprites.left)
    val btnLeftActive = buttonModel(buttonSprites.leftActive)
    val btnRight = buttonModel(buttonSprites.right)
    val btnRightActive = buttonModel(buttonSprites.rightActive)
    val btnMiddle = buttonModel(buttonSprites.middle)
    val btnMiddleActive = buttonModel(buttonSprites.middleActive)
    val btnMiddleLg = buttonModel(buttonSprites.middleLg)
    val btnMiddleLgActive = buttonModel(buttonSprites.middleLgActive)
    val btnMiddleRoundLg = buttonModel(buttonSprites.middleRoundLg)
    val btnMiddleRoundLgActive = buttonModel(buttonSprites.middleRoundLgActive)

    object Vertical {
        val btnTop = buttonModel(buttonVerticalSprites.left)
        val btnTopActive = buttonModel(buttonVerticalSprites.leftActive)
        val btnBottom = buttonModel(buttonVerticalSprites.right)
        val btnBottomActive = buttonModel(buttonVerticalSprites.rightActive)
        val btnMiddle = buttonModel(buttonVerticalSprites.middle)
        val btnMiddleActive = buttonModel(buttonVerticalSprites.middleActive)
        val btnMiddleLg = buttonModel(buttonVerticalSprites.middleLg)
        val btnMiddleLgActive = buttonModel(buttonVerticalSprites.middleLgActive)
        val btnMiddleRoundLg = buttonModel(buttonVerticalSprites.middleRoundLg)
        val btnMiddleRoundLgActive = buttonModel(buttonVerticalSprites.middleRoundLgActive)
    }

    object Pairs {
        val left = btnLeft to btnLeftActive
        val right = btnRight to btnRightActive
        val middle = btnMiddle to btnMiddleActive
        val middleLg = btnMiddleLg to btnMiddleLgActive
        val middleRoundLg = btnMiddleRoundLg to btnMiddleRoundLgActive
    }

    object VerticalPairs {
        val top = Vertical.btnTop to Vertical.btnTopActive
        val bottom = Vertical.btnBottom to Vertical.btnBottomActive
        val middle = Vertical.btnMiddle to Vertical.btnMiddleActive
        val middleLg = Vertical.btnMiddleLg to Vertical.btnMiddleLgActive
        val middleRoundLg = Vertical.btnMiddleRoundLg to Vertical.btnMiddleRoundLgActive
    }

    private fun buttonModel(layer: Texture, base: ModelResource = baseButton) = createModel {
        parent(base)
        textures(
            "layer0" to layer
        )
    }
}