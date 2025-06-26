package org.everbuild.celestia.orion.platform.minestom.command.debug

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer

object DebugDebugger : Debugger {
    override val identifier: String = "orion/debug"

    @Debuggable
    fun testArguments(player: Player, int: Int, orionPlayer: OrionPlayer) {
        player.sendMessage("Got args: $int, ${orionPlayer.name}")
    }
}