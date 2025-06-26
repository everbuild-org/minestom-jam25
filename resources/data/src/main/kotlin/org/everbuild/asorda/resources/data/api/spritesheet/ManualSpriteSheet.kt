package org.everbuild.asorda.resources.data.api.spritesheet

import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.texture.ImageSource

abstract class ManualSpriteSheet(source: ImageSource, private val itemsW: Int, private val itemsH: Int) : SpriteSheet {
    private val image = source.load()
    private val itemWidth = image.width / itemsW
    private val itemHeight = image.height / itemsH

    override fun item(x: Int, y: Int): SpriteTexture {
        val minX = x * itemWidth
        val minY = y * itemHeight

        return SpriteTexture(image.getSubimage(minX, minY, itemWidth, itemHeight))
    }

    override fun forEach(action: (x: Int, y: Int, value: Texture) -> Unit) {
        for (y in 0 until itemsH) {
            for (x in 0 until itemsW) {
                action(x, y, item(x, y))
            }
        }
    }
}
