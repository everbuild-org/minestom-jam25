package org.everbuild.jam25.world

import net.minestom.server.coordinate.Pos
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.chunksInRange
import org.everbuild.jam25.Jam

class LobbyWorld : ZippedWorld("lobby") {
    init {
        val jamBiome = Mc.biome.getId(Jam.oilBiome)
        instance.chunksInRange(Pos.ZERO, 4).forEach { (x, z) ->
            val chunk = instance.loadChunk(x, z).join()
            synchronized(chunk) {
                for (section in chunk.sections) {
                    section.biomePalette().fill(jamBiome)
                }
            }
        }
    }
}
