package org.everbuild.celestia.orion.core.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.util.ARGBLike
import net.minestom.server.color.AlphaColor
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.slf4j.LoggerFactory
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO
import org.everbuild.celestia.orion.core.util.httpClient
import org.http4k.core.Method
import org.http4k.core.Request

class CachedChatTexture(path: URI, overlayedCoordinates: List<ChatTexViewBox>) {
    private val eightByEightImage = BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB)
    var chatText: Component
        private set

    private val logger = LoggerFactory.getLogger(CachedChatTexture::class.java)

    private val resourceSpacingMin1 = OrionPacks.getCharacterCodepoint("spacing_-1")
    private val resourceSpacingMin2 = OrionPacks.getCharacterCodepoint("spacing_-2")
    private val resourceDots = listOf(
        OrionPacks.getCharacterCodepoint("white_px_7"),
        OrionPacks.getCharacterCodepoint("white_px_6"),
        OrionPacks.getCharacterCodepoint("white_px_5"),
        OrionPacks.getCharacterCodepoint("white_px_4"),
        OrionPacks.getCharacterCodepoint("white_px_3"),
        OrionPacks.getCharacterCodepoint("white_px_2"),
        OrionPacks.getCharacterCodepoint("white_px_1"),
        OrionPacks.getCharacterCodepoint("white_px_0"),
    )

    init {
        // HTTP Get texture from path
        logger.info("Fetching texture from $path")
        val binaryData = httpClient(Request(Method.GET, path.toURL().toString())).body.payload.array()
        val image = ImageIO.read(binaryData.inputStream())

        // new black image
        for (x in 0 until 8) {
            for (y in 0 until 8) {
                eightByEightImage.setRGB(x, y, 0xFF000000.toInt())
            }
        }

        val g: Graphics = eightByEightImage.graphics

        // overlay image
        for (box in overlayedCoordinates) {
            val cropped = image.getSubimage(box.minX, box.minY, 8, 8)
            g.drawImage(cropped, 0, 0, null)
        }

        g.dispose()

        chatText = Component.text("")

        for (x in 0 until 8) {
            for (y in 0 until 8) {
                val color = TextColor.color(eightByEightImage.getRGB(x, y))
                chatText = chatText.append(
                    resourceDots[y]
                        .component()
                        .color(color)
                        .shadowColor(AlphaColor(0, 255, 255, 255))
                ).append(
                    resourceSpacingMin2.component()
                )
            }

            chatText = chatText.append(resourceSpacingMin1.component())
        }
    }
}