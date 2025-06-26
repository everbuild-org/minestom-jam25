package org.everbuild.asorda.resources.data.api.spritesheet

import org.everbuild.asorda.resources.data.api.texture.Texture

interface SpriteSheet {
    fun item(x: Int, y: Int): Texture
    fun forEach(action: (x: Int, y: Int, value: Texture) -> Unit)
}
