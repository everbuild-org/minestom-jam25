package org.everbuild.asorda.resources.data.api.texture

import org.everbuild.asorda.resources.data.api.resource
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ResourceImageSource(val path: String) : ImageSource {
    override fun load(): BufferedImage {
        try {
            return ImageIO.read(resource("$path.png").toByteArray().inputStream())
        } catch (e: NullPointerException) {
            throw RuntimeException("Resource not found: $path", e)
        }
    }
}