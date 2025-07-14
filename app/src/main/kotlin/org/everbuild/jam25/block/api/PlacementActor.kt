package org.everbuild.jam25.block.api

import net.minestom.server.entity.Player
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.ingame.GameTeam

sealed interface PlacementActor {
    fun getTeam(): GameTeam?

    data class ByPlayer(val player: Player) : PlacementActor {
        override fun getTeam(): GameTeam? = Jam.gameStates.getInGamePhase(player)?.teamOf(player)
    }
    data class ByTeam(val innerTeam: GameTeam): PlacementActor {
        override fun getTeam(): GameTeam = innerTeam
    }
    object ByServer: PlacementActor {
        override fun getTeam(): GameTeam? = null
    }
}