package org.everbuild.asorda.resources.data.api.texture

import java.awt.image.BufferedImage

interface ImageSource {
    fun load(): BufferedImage
}