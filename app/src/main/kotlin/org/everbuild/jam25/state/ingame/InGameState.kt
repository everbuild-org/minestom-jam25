package org.everbuild.jam25.state.ingame

import java.util.UUID
import java.util.concurrent.CompletableFuture
import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.chunk.ChunkSupplier
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.GlobalTickEvent
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.GameState
import org.everbuild.jam25.state.lobby.LobbyGroup
import org.everbuild.jam25.util.background
import org.everbuild.jam25.world.GameWorld
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement

class InGameState(lobby: LobbyGroup) : GameState {
    private val id = UUID.randomUUID()
    private val key = Key.key("jam", "in-game/$id")
    private val players = mutableListOf<Player>()
    private val audience = DynamicGroup { players.contains(it) }
    val world = GameWorld()
    val teamRed: GameTeam
    val teamBlue: GameTeam
    val teams: List<GameTeam>
    val advanceable = mutableSetOf<AdvanceableWorldElement>()

    private val instanceEvents = EventNode.event("in-game/$id/instance", EventFilter.INSTANCE) {
        it.instance == world.instance
    }

    private val playerEvents = EventNode.event("in-game/$id/player", EventFilter.PLAYER) {
        players.contains(it.player)
    }

    private val eventNode = EventNode.all("in-game/$id")
        .addChild(instanceEvents)
        .addChild(playerEvents)
        .listen<GlobalTickEvent, _> {
            advanceable.forEach { it.advance(world.instance) }
        }

    init {
        players.addAll(lobby.players)
        val teamSize = players.size / 2
        val redPlayers = players.subList(0, teamSize)
        val bluePlayers = players.subList(teamSize, players.size)
        teamRed = GameTeam(redPlayers, GameTeamType.RED)
        teamBlue = GameTeam(bluePlayers, GameTeamType.BLUE)
        teams = listOf(teamRed, teamBlue)

        teamRed.setInstance(world.instance, teamRed.poi.spawn)
        teamBlue.setInstance(world.instance, teamBlue.poi.spawn)

        advanceable.add(teamRed.poi.turret)
        advanceable.add(teamBlue.poi.turret)

        val jobs = mutableListOf<CompletableFuture<*>>()
        for (x in -10..10) {
            for (z in -10..10) {
                jobs.add(world.instance.loadChunk(x, z))
            }
        }
        CompletableFuture.allOf(*jobs.toTypedArray()).join()

        background {
            teams.map { it.poi }.forEach {
                it.turret.spawn(world.instance)
            }
        }
    }

    fun teamOf(player: Player): GameTeam? = teams.find { it.players.contains(player) }

    override fun events(): EventNode<out Event> = eventNode

    override fun players(): List<Player> = players
    override fun key(): Key = key
}