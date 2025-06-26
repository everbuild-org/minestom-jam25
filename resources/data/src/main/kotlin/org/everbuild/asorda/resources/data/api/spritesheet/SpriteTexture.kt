package org.everbuild.asorda.resources.data.api.spritesheet

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.imageio.ImageIO
import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.pathKey
import org.everbuild.asorda.resources.data.api.texture.BufferImageSource
import org.everbuild.asorda.resources.data.api.texture.ImageSource
import org.everbuild.asorda.resources.data.api.texture.ResourceImageSource
import org.everbuild.asorda.resources.data.api.withFileType
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Writable

class SpriteTexture(private val bufferedImage: BufferedImage) : Texture {
    override val key: Key = UUID.randomUUID().toString().pathKey("item/sprites")

    override fun getAsImageSource(): ImageSource {
        return BufferImageSource(bufferedImage)
    }

    override fun buildInto(pack: ResourcePack) {
        val stream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", stream)
        pack.texture(key.withFileType("png"), Writable.bytes(stream.toByteArray()))
    }
}
