package org.everbuild.jam25.state

import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent

class GameStateController {
    private var controlledStates = mutableListOf<GameState>()
    private val lobbyState = LobbyGameState()

    fun addPlayer(player: Player) {

    }

    fun addPlayer(player: AsyncPlayerConfigurationEvent) {
        lobbyState.addPlayer(player.player)
    }
}