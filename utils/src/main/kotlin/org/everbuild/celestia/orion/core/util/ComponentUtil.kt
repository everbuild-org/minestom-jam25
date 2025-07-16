package org.everbuild.celestia.orion.core.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.entity.Player
import net.minestom.server.network.ConnectionState

private val minimessage = MiniMessage.miniMessage()

fun String.component() = Component.text(this)
fun String.minimessage() = minimessage.deserialize(this)
fun Audience.sendMiniMessage(message: String) = this.sendMessage(minimessage.deserialize(message))
fun Audience.sendMiniMessageActionBar(message: String) {
    this.forEachAudience {
        if (it !is Player) return@forEachAudience
        if (it.playerConnection.connectionState != ConnectionState.PLAY) return@forEachAudience
        this.sendActionBar(minimessage.deserialize(message))
    }
}

operator fun Component.plus(component: Component) = this.append(component)