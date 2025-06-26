package org.everbuild.celestia.orion.core.database.playerdata

import org.everbuild.celestia.orion.core.database.database
import org.everbuild.celestia.orion.core.onlinetime.OnlineTimeInterpolator
import org.everbuild.celestia.orion.core.platform.PlatformIndependentJoinEvent
import org.ktorm.entity.add
import java.time.Instant

fun onPlayerdataPlayerJoin(event: PlatformIndependentJoinEvent) {
    val player = PlayerLoader.loadNew(event.playerUUID)
    if (player == null) {
        database.orionPlayers.add(OrionPlayer {
            this.name = event.playerName
            this.uuid = event.playerUUID.toString()
            this.locale = event.playerLocale
        })
        PlayerLoader.loadNew(event.playerUUID)
    } else {
        player.lastLogin = Instant.now()
        player.flushChanges()
        OnlineTimeInterpolator.registerJoin(player)
    }
}