package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.spritesheet.ManualSpriteSheet
import org.everbuild.asorda.resources.data.api.texture.ResourceImageSource
import org.everbuild.asorda.resources.data.api.texture.Texture

object InteractionMenu : ContentList("interaction_menu") {
    fun interactionOverlay(ident: String, tex: String) = createBitmapCharacter(ident) {
        texture(Texture("interaction_menu/$tex"))
        height(140)
        ascent(83)
    }

    val defaultRadial = interactionOverlay("interaction_radial", "radial_menu")
    val toolTip = interactionOverlay("interaction_radial_tt", "radial_menu_tooltip")
    val leftClick = interactionOverlay("leftclick_tt", "leftclick")
    val rightClick = interactionOverlay("rightclick_tt", "rightclick")
}