package org.everbuild.jam25

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.ping.Status
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.util.listen


object PingResponder {
    private val favicon: ByteArray

    init {
        val image: BufferedImage = ImageIO.read(PingResponder.javaClass.getResourceAsStream("/server-icon.png"))
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        favicon = outputStream.toByteArray()
        outputStream.close()
    }

    fun eventNode(): EventNode<Event> = EventNode.all("ping")
        .listen<ServerListPingEvent, _> { event ->
            event.status = Status.builder()
                .description("<gradient:#FFAA00:#FF5555><bold>✧ Asorda Jam Entry ✧</bold></gradient> \n<white>Wi1helm</white> <b>|</b> <white>p3sto</white> <b>|</b> <white>Team Asorda</white>".minimessage())
                .favicon(favicon)
                .build()
        }
}