package org.everbuild.asorda.resources.data.api.texture.textrendering

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import org.everbuild.asorda.resources.data.api.resourceFile
import org.everbuild.asorda.resources.data.api.texture.ImageSource

class RenderedTextImageSource(
    private val width: Int,
    private val height: Int,
    private val x: Int,
    private val y: Int,
    private val text: String,
    private val fontSize: Int = 8,
    private val mainColor: Color = Color.WHITE,
    private val shadow: Boolean = true,
    private val fontPath: File = resourceFile("utils/Mojang-Regular.ttf")
) : ImageSource {
    override fun load(): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        g2d.composite = AlphaComposite.Clear
        g2d.fillRect(0, 0, width, height)
        g2d.composite = AlphaComposite.SrcOver

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        val font: Font = try {
            Font.createFont(Font.TRUETYPE_FONT, fontPath)
                .deriveFont(fontSize.toFloat())
        } catch (e: Exception) {
            Font("Serif", Font.PLAIN, fontSize)
        }
        g2d.font = font

        val shadowColor = Color(
            mainColor.red / 4,
            mainColor.green / 4,
            mainColor.blue / 4,
            127
        )

        if (shadow) {
            g2d.color = shadowColor
            g2d.drawString(text, (x + 1).toFloat(), (y + 1).toFloat())
        }

        g2d.color = mainColor
        g2d.drawString(text, x.toFloat(), y.toFloat())

        g2d.dispose()
        return image
    }
}