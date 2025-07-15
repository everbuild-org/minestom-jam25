package org.everbuild.jam25.state

import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.core.util.sendMiniMessageActionBar
import org.everbuild.celestia.orion.platform.minestom.api.utils.pling
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.celestia.orion.platform.minestom.util.scheduler
import org.everbuild.jam25.Jam
import org.everbuild.jam25.TabListController
import kotlin.time.Duration.Companion.seconds

class SpectatorGameState(
    private val gameInstance: Instance,
    private val spawnPosition: Pos
) : GameState {

    private val spectators = mutableListOf<Player>()

    private val eventNode =
        EventNode.type(this.key().asString(), EventFilter.PLAYER) { _, player -> spectators.contains(player) }
            .listen<PlayerDisconnectEvent, _> { event ->
                removeSpectator(event.player)
                Jam.gameStates.removeSpectatorFromGame(event.player)
            }
            .listen<PlayerMoveEvent, _> { event ->
                if (event.newPosition.y < -100.0) {
                    event.player.teleport(spawnPosition)
                    event.player.pling()
                }
            }

    init {
        scheduler(1.seconds, ::process)
    }

    override fun events(): EventNode<out Event> = eventNode

    override fun players(): List<Player> = spectators

    override fun key(): Key = Key.key("jam", "spectator")

    fun addSpectator(player: Player) {
        if (spectators.contains(player)) return
        spectators.add(player)
        player.sendMiniMessage("<yellow>Teleporting to game as spectator...</yellow>")
        if (player.instance != gameInstance) {
            player.setInstance(gameInstance, spawnPosition).thenRun {
                player.gameMode = GameMode.SPECTATOR
                TabListController.setSpectator(player, true)
                player.sendMiniMessage("<gray>You are now in Spectator Mode in the game!</gray>")
            }
        } else {
            1.seconds later {
                player.teleport(spawnPosition)
                player.gameMode = GameMode.SPECTATOR
                TabListController.setSpectator(player, true)
                player.sendMiniMessage("<gray>You are now in Spectator Mode in the game!</gray>")
                player.instance.players.forEach {
                    it.addViewer(player)
                }
                player.isInvisible = true
            }
        }
    }

    fun removeSpectator(player: Player) {
        if (!spectators.contains(player)) return
        spectators.remove(player)
        TabListController.setSpectator(player, false)
        player.gameMode = GameMode.SURVIVAL
        player.isInvisible = false
    }

    private fun process() {
        spectators.forEach { spectator ->
            if (spectator.gameMode != GameMode.SPECTATOR) {
                spectator.gameMode = GameMode.SPECTATOR
                println("Fixed gamemode for spectator ${spectator.username}")
            }
            spectator.sendMiniMessageActionBar("<gray>Spectator Mode • Press F5 to change view</gray>")
        }
    }

    fun teleportToPlayer(spectator: Player, target: Player) {
        if (!spectators.contains(spectator)) return
        spectator.teleport(target.position.add(0.0, 1.0, 0.0))
        spectator.sendMiniMessageActionBar("<yellow>You are now following ${target.username}</yellow>")
    }

    fun getSpectatorCount(): Int = spectators.size
    fun isSpectator(player: Player): Boolean = spectators.contains(player)

    companion object {
        fun createForGame(gameInstance: Instance, spawnPosition: Pos): SpectatorGameState {
            return SpectatorGameState(gameInstance, spawnPosition)
        }
    }
}