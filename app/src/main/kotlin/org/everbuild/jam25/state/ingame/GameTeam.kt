package org.everbuild.jam25.state.ingame

import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.scoreboard.tabListExtras
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.Jam
import org.everbuild.jam25.world.shield.ShieldRenderer

class GameTeam(val players: List<Player>, val type: GameTeamType) : DynamicGroup({ players.contains(it) }) {
    val poi = type.poi()
    var shield: ShieldRenderer? = null

    init {
        players.forEach {
            tabListExtras[it] = type.short + " "
        }
    }

    fun spawnShield(instance: Instance) {
        shield = ShieldRenderer(instance, poi.mainShield.toVertices())
    }

    fun initOilBiome(instance: Instance) {
        val id = Mc.biome.getId(Jam.oilBiome)
        poi.oilChunks.forEachVSliceInChunk { cx, cz, containingVSlices ->
            val chunk = instance.loadChunk(cx, cz).join()
            synchronized(chunk) {
                for (section in chunk.sections) {
                    section.biomePalette().fill(id)
                }
            }
        }
    }
}