package org.everbuild.jam25.state

import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import org.everbuild.celestia.orion.platform.minestom.util.listen

class GameStateController {
    private var controlledStates = mutableListOf<GameState>()
    private val lobbyState = LobbyGameState()
    private val node = EventNode.all("game-state-controller")
        .listen<AsyncPlayerConfigurationEvent, _> {
            addPlayer(it)
        }

    fun addPlayer(player: Player) {
        lobbyState.addPlayer(player)
        player.setInstance(lobbyState.getInstance(), lobbyState.getSpawn())
    }

    fun addPlayer(event: AsyncPlayerConfigurationEvent) {
        lobbyState.addPlayer(event.player)
        event.spawningInstance = lobbyState.getInstance()
        event.player.respawnPoint = lobbyState.getSpawn()
    }

    fun eventNode() = node
}