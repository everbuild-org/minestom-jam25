package org.everbuild.jam25

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.core.util.sendMiniMessageActionBar
import org.everbuild.celestia.orion.platform.minestom.api.Mc


open class DynamicGroup(val block: (Player) -> Boolean) {
    fun sendMiniMessage(minimessage: String) {
        forEach { it.sendMiniMessage(minimessage) }
    }

    fun sendMiniMessageTitle(title: String, subtitle: String) {
        forEach {
            it.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                500.milliseconds.toJavaDuration(),
                4.seconds.toJavaDuration(),
                500.milliseconds.toJavaDuration()
            ))
            it.sendTitlePart(TitlePart.SUBTITLE, subtitle.minimessage())
            it.sendTitlePart(TitlePart.TITLE, title.minimessage())
        }
    }

    fun sendMiniMessageActionBar(minimessage: String) {
        forEach { it.sendMiniMessageActionBar(minimessage) }
    }

    fun forEach(inlineBlock: (Player) -> Unit) {
        Mc.connection.onlinePlayers.filter(block).forEach { inlineBlock(it) }
    }

    fun setInstance(instance: InstanceContainer, pos: Pos) {
        forEach {
            if (it.instance != instance) {
                it.setInstance(instance, pos)
            }
        }
    }
}