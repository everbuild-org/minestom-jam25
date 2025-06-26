package org.everbuild.asorda.resources.data.api.spritesheet

import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.texture.ImageSource

class ButtonAutoTileSpriteSheet(source: ImageSource) : SpriteSheet {
    private val image = source.load()
    private val tileWidth = 18
    private val tileHeight = 18
    private val offset = 1
    private val tilesX = image.width / tileWidth
    private val tilesY = image.height / tileHeight
    private val tiles = arrayListOf<Texture>()

    init {
        repeat(tilesY) { y ->
            repeat(tilesX) { x ->
                val tile = image.getSubimage(
                    x * tileWidth,
                    y * tileHeight,
                    tileWidth + offset,
                    tileHeight + offset
                )

                tiles.add(SpriteTexture(tile))
            }
        }
    }

    override fun item(x: Int, y: Int): Texture {
        return tiles[y * tilesX + x]
    }

    override fun forEach(action: (x: Int, y: Int, value: Texture) -> Unit) {
        for (y in 0 until tilesY) {
            for (x in 0 until tilesX) {
                action(x, y, item(x, y))
            }
        }
    }
}