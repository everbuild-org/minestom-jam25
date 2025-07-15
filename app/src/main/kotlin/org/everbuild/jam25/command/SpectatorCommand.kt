package org.everbuild.jam25.command

import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.jam25.Jam
import org.everbuild.jam25.TabListController
import org.everbuild.jam25.state.SpectatorGameState
import org.everbuild.jam25.state.ingame.InGameState
import org.everbuild.jam25.state.lobby.LobbyGroup

object SpectatorCommand : Kommand("spectator", "spec") {
    private val argPlayer = ArgumentType.String("player")

    init {
        permission = "jam.spectator"
        default { player, _ ->
            handleSpectatorToggle(player)
        }
        executes(argPlayer) {
            val targetName = argPlayer()

            if (isAction(targetName)) {
                handleSpectatorAction(player, targetName)
            } else {
                handleTeleportToPlayer(player, targetName)
            }
        }
    }

    private fun isAction(input: String): Boolean {
        return input.lowercase() in listOf("list", "players", "info", "leave", "exit", "help")
    }

    private fun handleSpectatorToggle(player: Player) {
        val inGameState = Jam.gameStates.getInGamePhase(player)
        val lobbyGroup = Jam.gameStates.getLobby(player)

        if (lobbyGroup != null) {
            handleLobbySpectatorToggle(player, lobbyGroup)
            return
        }

        if (inGameState != null) {
            handleInGameSpectatorToggle(player, inGameState)
            return
        }

        player.sendMiniMessage("<red>You must be in lobby or in an active game to use Spectator Mode!</red>")
    }

    private fun handleLobbySpectatorToggle(player: Player, lobbyGroup: LobbyGroup) {
        if (Jam.gameStates.isLobbySpectator(player)) {
            Jam.gameStates.removeLobbySpectator(player)
            player.sendMiniMessage("<green>You are no longer marked as spectator! You will join the next game.</green>")
        } else {
            Jam.gameStates.addLobbySpectator(player)
            player.sendMiniMessage("<gray>You are now marked as spectator! You will observe the next game.</gray>")
            player.sendMiniMessage("<yellow>You will be automatically moved to spectator mode when the game starts.</yellow>")
            player.sendMiniMessage("<yellow>Your game mode will remain Survival until the game begins.</yellow>")
        }
    }

    private fun handleInGameSpectatorToggle(player: Player, inGameState: InGameState) {
        val spectatorState = Jam.gameStates.getOrCreateSpectatorState(inGameState)

        if (spectatorState.isSpectator(player)) {
            spectatorState.removeSpectator(player)
            val team = inGameState.teamOf(player)
            if (team != null) {
                if (player.instance != inGameState.world.instance) {
                    player.setInstance(inGameState.world.instance, team.poi.spawn)
                } else {
                    player.teleport(team.poi.spawn)
                }
            } else {
                if (player.instance != inGameState.world.instance) {
                    player.setInstance(inGameState.world.instance, Pos(0.0, 100.0, 0.0))
                } else {
                    player.teleport(Pos(0.0, 100.0, 0.0))
                }
            }
            player.sendMiniMessage("<green>You are no longer in spectator mode!</green>")
        } else {
            player.sendMiniMessage("<red>You cannot become a spectator during an active game!</red>")
            player.sendMiniMessage("<yellow>Use /spectator in the lobby to become a spectator for the next game.</yellow>")
        }
    }

    private fun handleTeleportToPlayer(spectator: Player, targetName: String) {
        val inGameState = Jam.gameStates.getInGamePhase(spectator)

        if (inGameState == null) {
            spectator.sendMiniMessage("<red>You must be in an active game to teleport!</red>")
            return
        }

        val spectatorState = Jam.gameStates.getOrCreateSpectatorState(inGameState)

        if (!spectatorState.isSpectator(spectator)) {
            spectator.sendMiniMessage("<red>You must be in Spectator Mode to teleport!</red>")
            return
        }

        val targetPlayer = Mc.connection.onlinePlayers.find {
            it.username.equals(targetName, ignoreCase = true)
        }

        if (targetPlayer == null) {
            spectator.sendMiniMessage("<red>Player '$targetName' was not found!</red>")
            return
        }

        if (!inGameState.players().contains(targetPlayer)) {
            spectator.sendMiniMessage("<red>Player '$targetName' is not in the game!</red>")
            return
        }

        spectatorState.teleportToPlayer(spectator, targetPlayer)
    }

    private fun handleSpectatorAction(player: Player, action: String) {
        val inGameState = Jam.gameStates.getInGamePhase(player)
        val lobbyGroup = Jam.gameStates.getLobby(player)

        when (action.lowercase()) {
            "info" -> {
                if (inGameState != null) {
                    val spectatorState = Jam.gameStates.getOrCreateSpectatorState(inGameState)
                    showSpectatorInfo(player, spectatorState)
                } else if (lobbyGroup != null) {
                    showLobbySpectatorInfo(player)
                } else {
                    player.sendMiniMessage("<red>You must be in lobby or in an active game!</red>")
                }
            }
            "list", "players" -> {
                if (inGameState != null) {
                    val spectatorState = Jam.gameStates.getOrCreateSpectatorState(inGameState)
                    showPlayerList(player, inGameState, spectatorState)
                } else if (lobbyGroup != null) {
                    showLobbyPlayerList(player, lobbyGroup)
                } else {
                    player.sendMiniMessage("<red>You must be in lobby or in an active game!</red>")
                }
            }
            "leave", "exit" -> {
                handleSpectatorToggle(player) // Wiederverwendung der Toggle-Logik
            }
            "help" -> {
                showHelp(player)
            }
            else -> {
                player.sendMiniMessage("<red>Unknown action: $action</red>")
                player.sendMiniMessage("<yellow>Use /spectator help for help.</yellow>")
            }
        }
    }

    private fun showLobbySpectatorInfo(player: Player) {
        player.sendMiniMessage("<yellow>════════ Lobby Spectator Info ════════</yellow>")
        
        if (Jam.gameStates.isLobbySpectator(player)) {
            player.sendMiniMessage("<white>Status: <gray>Marked as Future Spectator</gray></white>")
            player.sendMiniMessage("<white>Current Game Mode: <green>Survival</green></white>")
            player.sendMiniMessage("<white>Next Game Role: <gray>Spectator</gray></white>")
        } else {
            player.sendMiniMessage("<white>Status: <green>Active Player</green></white>")
            player.sendMiniMessage("<white>Current Game Mode: <green>Survival</green></white>")
            player.sendMiniMessage("<white>Next Game Role: <green>Player</green></white>")
        }
        
        player.sendMiniMessage("<yellow>You will be moved to spectator mode when the game starts.</yellow>")
    }

    private fun showLobbyPlayerList(player: Player, lobbyGroup: org.everbuild.jam25.state.lobby.LobbyGroup) {
        val activePlayers = lobbyGroup.getActivePlayers()
        val spectators = lobbyGroup.players.filter { Jam.gameStates.isLobbySpectator(it) }

        player.sendMiniMessage("<yellow>════════ Lobby Player List ════════</yellow>")
        player.sendMiniMessage("<green>Active Players (${activePlayers.size}):</green>")

        if (activePlayers.isEmpty()) {
            player.sendMiniMessage("<gray>  No active players</gray>")
        } else {
            activePlayers.forEach { activePlayer ->
                player.sendMiniMessage("<white>  • ${activePlayer.username}</white>")
            }
        }

        player.sendMiniMessage("<gray>Future Spectators (${spectators.size}):</gray>")
        if (spectators.isEmpty()) {
            player.sendMiniMessage("<gray>  No future spectators</gray>")
        } else {
            spectators.forEach { spectator ->
                player.sendMiniMessage("<gray>  • ${spectator.username} <dark_gray>(will spectate)</dark_gray></gray>")
            }
        }
    }

    private fun showPlayerList(player: Player, inGameState: InGameState, spectatorState: SpectatorGameState) {
        val activePlayers = inGameState.players().filter { !spectatorState.isSpectator(it) }
        val spectators = spectatorState.players()

        player.sendMiniMessage("<yellow>════════ Player List ════════</yellow>")
        player.sendMiniMessage("<green>Active Players (${activePlayers.size}):</green>")

        if (activePlayers.isEmpty()) {
            player.sendMiniMessage("<gray>  No active players</gray>")
        } else {
            activePlayers.forEach { activePlayer ->
                val team = inGameState.teamOf(activePlayer)
                val teamColor = if (team?.type?.name == "RED") "<red>" else "<blue>"
                player.sendMiniMessage("<white>  • $teamColor${activePlayer.username}</white>")
            }
        }

        player.sendMiniMessage("<gray>Spectators (${spectators.size}):</gray>")
        if (spectators.isEmpty()) {
            player.sendMiniMessage("<gray>  No spectators</gray>")
        } else {
            spectators.forEach { spectator ->
                player.sendMiniMessage("<gray>  • ${spectator.username}</gray>")
            }
        }

        player.sendMiniMessage("<yellow>Use /spectator <playername> to teleport to a player</yellow>")
    }

    private fun showSpectatorInfo(player: Player, spectatorState: SpectatorGameState) {
        player.sendMiniMessage("<yellow>════════ Spectator Info ════════</yellow>")
        player.sendMiniMessage("<white>Status: ${if (spectatorState.isSpectator(player)) "<gray>Spectator</gray>" else "<green>Active Player</green>"}</white>")
        player.sendMiniMessage("<white>Spectator Count: <yellow>${spectatorState.getSpectatorCount()}</yellow></white>")
        player.sendMiniMessage("<white>TabList Status: ${if (TabListController.isHidden(player)) "<gray>Hidden</gray>" else "<green>Visible</green>"}</white>")

        val inGameState = Jam.gameStates.getInGamePhase(player)
        if (inGameState != null) {
            val team = inGameState.teamOf(player)
            if (team != null) {
                val teamColor = if (team.type.name == "RED") "<red>" else "<blue>"
                player.sendMiniMessage("<white>Team: $teamColor${team.type.name}</white>")
            }
        }
    }

    private fun showHelp(player: Player) {
        player.sendMiniMessage("<yellow>════════ Spectator Help ════════</yellow>")
        player.sendMiniMessage("<white>/spectator</white> - Toggle spectator mode")
        player.sendMiniMessage("<white>/spectator <player></white> - Teleport to a player (spectators only)")
        player.sendMiniMessage("<white>/spectator list</white> - Show all players")
        player.sendMiniMessage("<white>/spectator info</white> - Show spectator information")
        player.sendMiniMessage("<white>/spectator leave</white> - Exit spectator mode")
        player.sendMiniMessage("<white>/spectator help</white> - Show this help")
        player.sendMiniMessage("<yellow>In lobby: Mark yourself as future spectator for the next game!</yellow>")
        player.sendMiniMessage("<red>Note: You cannot become a spectator during an active game!</red>")
    }
}