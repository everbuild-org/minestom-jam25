package org.everbuild.asorda.resources.data.api.texture

import java.awt.image.BufferedImage

class BufferImageSource(val image: BufferedImage) : ImageSource {
    override fun load(): BufferedImage {
        return image
    }
}