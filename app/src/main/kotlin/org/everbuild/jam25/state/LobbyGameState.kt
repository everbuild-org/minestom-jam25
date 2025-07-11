package org.everbuild.jam25.state

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.chunk.ChunkSupplier
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.listen

class LobbyGameState : GameState, DynamicGameState {
    private val eventNode = EventNode.type(this.key().asString(), EventFilter.PLAYER) { _, player -> players.contains(player) }
        .listen<PlayerDisconnectEvent, _> { event -> players.remove(event.player) }

    private val instance = Mc.instance.createInstanceContainer().also {
        it.chunkSupplier = ChunkSupplier {i, x, y -> LightingChunk(i, x, y) }
        it.setGenerator { unit->unit.modifier().fillHeight(0, 32, Block.STONE) }
    }

    private val players = mutableListOf<Player>()
    private val audience = Audiences.players { players.contains(it) }

    override fun events(): EventNode<out Event> = eventNode

    override fun audience(): Audience = audience

    override fun players(): List<Player> = players

    override fun addPlayer(player: Player) {
        players.add(player)
    }

    override fun getInstance(): Instance {
        TODO("Not yet implemented")
    }

    override fun key(): Key = Key.key("jam", "lobby")
}