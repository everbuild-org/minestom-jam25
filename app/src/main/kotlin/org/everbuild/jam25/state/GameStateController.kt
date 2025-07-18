package org.everbuild.jam25.state

import kotlin.time.Duration.Companion.seconds
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.platform.minestom.scoreboard.tabListExtras
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.state.ingame.InGameState
import org.everbuild.jam25.state.lobby.LobbyGameState
import org.everbuild.jam25.state.lobby.LobbyGroup

class GameStateController {
    private var controlledStates = mutableListOf<GameState>()
    val lobbyState = LobbyGameState()
    private val lobbySpectators = mutableSetOf<Player>()
    private val spectatorStates = mutableMapOf<InGameState, SpectatorGameState>()
    private val spectatorToGame = mutableMapOf<Player, InGameState>()
    private val node = EventNode.all("game-state-controller")
        .addChild(lobbyState.events())
        .listen<AsyncPlayerConfigurationEvent, _> {
            addPlayer(it)
        }

    fun addPlayer(player: Player) {
        lobbyState.addPlayer(player)
        if (player.instance != lobbyState.getInstance()) player.setInstance(lobbyState.getInstance(), lobbyState.getSpawn())
        else player.teleport(lobbyState.getSpawn())

        tabListExtras[player] = ""
    }

    fun addPlayer(event: AsyncPlayerConfigurationEvent) {
        lobbyState.addPlayer(event.player)
        event.spawningInstance = lobbyState.getInstance()
        event.player.respawnPoint = lobbyState.getSpawn()
    }

    fun eventNode() = node

    fun transitionIntoPlay(group: LobbyGroup) {
        val spectators = group.players.filter { lobbySpectators.contains(it) }
        
        spectators.forEach { spectator ->
            group.removePlayer(spectator)
            lobbySpectators.remove(spectator)
        }
        lobbyState.remove(group)
        val inGameState = InGameState(group)
        controlledStates.add(inGameState)
        node.addChild(inGameState.events())
        spectators.forEach { spectator ->
            spectatorToGame[spectator] = inGameState // Spectator zu Spiel zuordnen
            val spectatorState = getOrCreateSpectatorState(inGameState)
            spectatorState.addSpectator(spectator)
            println("Added spectator ${spectator.username} to game")
        }
    }

    fun tryQuickStart(player: Player) {
        if (!lobbyState.players().contains(player)) return
        lobbyState.tryQuickStart(player)
    }

    fun getInGamePhase(player: Player): InGameState? {
        val activeGame = controlledStates.find { it is InGameState && it.players().contains(player) } as? InGameState
        if (activeGame != null) return activeGame
        return spectatorToGame[player]
    }

    fun getInGamePhase(instance: Instance): InGameState? {
        return controlledStates.find { it is InGameState && it.world.instance == instance } as? InGameState
    }

    fun getLobby(player: Player): LobbyGroup? {
        return lobbyState.getLobby(player)
    }

    fun addLobbySpectator(player: Player) {
        lobbySpectators.add(player)
    }

    fun removeLobbySpectator(player: Player) {
        lobbySpectators.remove(player)
    }

    fun isLobbySpectator(player: Player): Boolean {
        return lobbySpectators.contains(player)
    }

    fun getOrCreateSpectatorState(inGameState: InGameState): SpectatorGameState {
        return spectatorStates.computeIfAbsent(inGameState) {
            val spectatorPos = Pos(0.0, 100.0, 0.0)
            SpectatorGameState.createForGame(inGameState.world.instance, spectatorPos)
        }
    }
    
    // Methode um Spectator aus dem System zu entfernen (bei Disconnect)
    fun removeSpectatorFromGame(player: Player) {
        spectatorToGame.remove(player)
    }

    fun endGame(game: InGameState, team: GameTeam) {
        team.sendMiniMessageTitle("<green>You won against ${team.opposite.type.long}!", "<gray>${team.opposite.formatNames()}")
        team.opposite.sendMiniMessageTitle("<red>You lost against ${team.type.long}!", "<gray>${team.formatNames()}")
        3.seconds later {
            dissolve(game)
        }
    }

    fun dissolve(game: InGameState) {
        game.players().forEach(::addPlayer)
        node.removeChild(game.events())
        game.dissolve()
        controlledStates.remove(game)
    }
}