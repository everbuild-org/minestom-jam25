package org.everbuild.jam25.state.ingame

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.scoreboard.tabListExtras
import org.everbuild.jam25.DynamicGroup

class GameTeam(val players: List<Player>, type: GameTeamType) : DynamicGroup({ players.contains(it) }) {
    init {
        players.forEach {
            tabListExtras[it] = type.short + " "
        }
    }
}