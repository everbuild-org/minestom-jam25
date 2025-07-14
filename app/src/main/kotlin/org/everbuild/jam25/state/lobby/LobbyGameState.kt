package org.everbuild.jam25.state.lobby

import kotlin.time.Duration.Companion.seconds
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.chunk.ChunkSupplier
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.pling
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.celestia.orion.platform.minestom.util.scheduler
import org.everbuild.jam25.state.GameState
import org.everbuild.jam25.world.LobbyWorld

class LobbyGameState : GameState {
    private val groups = mutableListOf<LobbyGroup>()

    private val eventNode =
        EventNode.type(this.key().asString(), EventFilter.PLAYER) { _, player -> players.contains(player) }
            .listen<PlayerDisconnectEvent, _> { event ->
                players.remove(event.player)
                groups.forEach { it.removePlayer(event.player) }
            }
            .listen<PlayerMoveEvent, _> { event ->
                if (event.newPosition.y < -75.0) {
                    event.player.teleport(getSpawn())
                    event.player.pling()
                }
            }

    private val world = LobbyWorld()
    private val instance = world.instance

    private val players = mutableListOf<Player>()

    init {
        scheduler(1.seconds, ::process)
    }

    override fun events(): EventNode<out Event> = eventNode

    override fun players(): List<Player> = players

    fun getInstance(): Instance = instance

    fun getSpawn(): Pos = Pos(2.5, -53.0, 10.5, -90f, 0f)

    fun addPlayer(player: Player) {
        players.add(player)
    }

    override fun key(): Key = Key.key("jam", "lobby")

    fun process() {
        players.filter { !groups.any { g -> g.containsPlayer(it) } }.forEach {
            val group = groups.filter { g -> g.hasSpace() }.maxByOrNull { g -> g.players.size }
            if (group != null) {
                group.addPlayer(it)
            } else {
                val newGroup = LobbyGroup()
                newGroup.addPlayer(it)
                groups.add(newGroup)
            }
        }

        groups.reversed().forEach { it.process() }
    }

    fun remove(group: LobbyGroup) {
        // Don't manage these users anymore
        group.players.forEach { players.remove(it) }

        // Don't update the group
        groups.remove(group)
    }

    fun tryQuickStart(player: Player) {
        val grp = groups.find { it.containsPlayer(player) } ?: return
        grp.startGame()
    }

    fun getLobby(player: Player): LobbyGroup? {
        return groups.firstOrNull { it.containsPlayer(player) }
    }

    companion object {
        const val MIN_PLAYERS_PER_INSTANCE = 2
        const val PLAYERS_PER_INSTANCE_SLOW_START = 4
        const val PLAYERS_PER_INSTANCE = 8
    }
}