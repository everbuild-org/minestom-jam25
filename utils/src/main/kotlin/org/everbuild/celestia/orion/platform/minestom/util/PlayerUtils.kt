package org.everbuild.celestia.orion.platform.minestom.util

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.c
import org.everbuild.celestia.orion.core.database.playerdata.t
import org.everbuild.celestia.orion.core.translation.TranslationContext
import org.everbuild.celestia.orion.core.util.asUuid

val Player.orion get() = org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader.load(this.uuid)!!
fun Player.t(key: String): TranslationContext = this.orion.t(key)
fun Player.c(key: String): Component = this.orion.c(key)

val OrionPlayer.minestom get(): Player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(this.uuid.asUuid())!!

fun Player.sendTranslated(key: String, resolver: (TranslationContext) -> Unit = {}) {
    sendMessage(t(key).also(resolver).c)
}