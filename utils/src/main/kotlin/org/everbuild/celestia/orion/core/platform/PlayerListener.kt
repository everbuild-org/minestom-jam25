package org.everbuild.celestia.orion.core.platform

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.remote.RemotePlatform

fun onPlatformPlayerJoin(event: PlatformIndependentJoinEvent) {
    val player = PlayerLoader.load(event.playerUUID) ?: return
    RemotePlatform.playerJoined(player)
}

fun onPlatformPlayerLeave(player: OrionPlayer) {
    RemotePlatform.playerLeft(player)
}