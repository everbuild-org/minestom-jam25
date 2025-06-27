package org.everbuild.jam25

import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.chunk.ChunkSupplier
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.Pos
import org.everbuild.celestia.orion.platform.minestom.util.listen

class Jam : OrionServer() {
    val instance = Mc.instance.createInstanceContainer().also {
        it.chunkSupplier = ChunkSupplier { x, y, c -> LightingChunk(x, y, c) }
        it.setGenerator { unit ->
            unit.modifier()
                .fillHeight(0, 32, Block.STONE)
        }
    }

    init {
        listen<AsyncPlayerConfigurationEvent> {
            it.spawningInstance = instance
            it.player.respawnPoint = Pos(0, 33, 0)
        }
    }
}

fun main() {
    Jam().bind()
}