package org.everbuild.asorda.resources.data.api.texture

import java.awt.image.BufferedImage

class OverlayImageSource(private val sources: Collection<ImageSource>) : ImageSource {
    constructor(vararg sources: ImageSource) : this(sources.asList())

    override fun load(): BufferedImage {
        val images = sources.map { it.load() }

        if (images.isEmpty()) {
            throw IllegalArgumentException("No image sources provided.")
        }

        val width = images[0].width
        val height = images[0].height

        for (image in images) {
            if (image.width != width || image.height != height) {
                println("[WARN] Image dimensions do not match.")
            }
        }

        val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = result.createGraphics()

        for (image in images) {
            g.drawImage(image, 0, 0, null)
        }

        g.dispose()
        return result
    }
}