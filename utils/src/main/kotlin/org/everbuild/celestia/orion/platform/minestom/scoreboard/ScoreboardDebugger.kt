package org.everbuild.celestia.orion.platform.minestom.scoreboard

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger

object ScoreboardDebugger : Debugger {
    override val identifier: String = "orion/scoreboard"

    @Debuggable
    fun playerTeams(player: Player, target: Player) {
        for (team in MinecraftServer.getTeamManager().teams) {
            if (team.players.contains(target)) {
                player.sendMessage("- ${team.teamName} ".component().append(team.teamDisplayName))
            }
        }
    }
}