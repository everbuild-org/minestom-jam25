package org.everbuild.jam25.state.ingame

import java.util.UUID
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
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.GameState
import org.everbuild.jam25.state.lobby.LobbyGroup

class InGameState(lobby: LobbyGroup) : GameState {
    private val id = UUID.randomUUID()
    private val key = Key.key("jam", "in-game/$id")
    private val players = mutableListOf<Player>()
    private val audience = DynamicGroup { players.contains(it) }
    private val instance = Mc.instance.createInstanceContainer().also {
        it.chunkSupplier = ChunkSupplier { i, x, y -> LightingChunk(i, x, y) }
        it.setGenerator { unit -> unit.modifier().fillHeight(0, 32, Block.STONE) }
    }
    private val teamRed: GameTeam
    private val teamBlue: GameTeam

    private val instanceEvents = EventNode.event("in-game/$id/instance", EventFilter.INSTANCE) {
        it.instance == instance
    }

    private val playerEvents = EventNode.event("in-game/$id/player", EventFilter.PLAYER) {
        players.contains(it.player)
    }

    private val eventNode = EventNode.all("in-game/$id")
        .addChild(instanceEvents)
        .addChild(playerEvents)

    init {
        players.addAll(lobby.players)
        val teamSize = players.size / 2
        val redPlayers = players.subList(0, teamSize)
        val bluePlayers = players.subList(teamSize, players.size)
        teamRed = GameTeam(redPlayers, GameTeamType.RED)
        teamBlue = GameTeam(bluePlayers, GameTeamType.BLUE)

        audience.sendMiniMessage("${Jam.PREFIX} <gray>Game started! Welcome to ${Jam.NAME}<gray>!")
        audience.setInstance(instance, Pos(0.0, 32.0, 0.0))
    }

    override fun events(): EventNode<out Event> = eventNode

    override fun players(): List<Player> = players

    override fun key(): Key = key
}