package org.everbuild.celestia.orion.platform.minestom.platform

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.platform.Teleportation
import org.everbuild.celestia.orion.core.platform.TeleportationFactory
import org.everbuild.celestia.orion.core.util.FullPosition
import org.everbuild.celestia.orion.platform.minestom.util.minestom
import org.everbuild.celestia.orion.platform.minestom.util.toFull

class MinestomTeleportationFactory : TeleportationFactory {
    override fun to(player: OrionPlayer): Teleportation {
        return object : Teleportation {
            override fun toLocation(): FullPosition = player.minestom.position.toFull()
        }
    }

    override fun to(pos: FullPosition): Teleportation {
        return object : Teleportation {
            override fun toLocation(): FullPosition = pos
        }
    }
}