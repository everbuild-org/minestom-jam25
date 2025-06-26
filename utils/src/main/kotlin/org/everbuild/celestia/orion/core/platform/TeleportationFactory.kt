package org.everbuild.celestia.orion.core.platform

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.util.FullPosition

interface TeleportationFactory {
    fun to(player: OrionPlayer): Teleportation
    fun to(pos: FullPosition): Teleportation
}