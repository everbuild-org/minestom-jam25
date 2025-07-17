package org.everbuild.jam25.state.ingame

import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.pling
import org.everbuild.celestia.orion.platform.minestom.scoreboard.tabListExtras
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.Jam
import org.everbuild.jam25.item.impl.HammerItem
import org.everbuild.jam25.item.impl.PipeBlockItem
import org.everbuild.jam25.missile.MissileController
import org.everbuild.jam25.missile.MissileControllerImpl
import org.everbuild.jam25.world.shield.ShieldRenderer

class GameTeam(val players: List<Player>, val type: GameTeamType, val game: InGameState) : DynamicGroup({ players.contains(it) }), MissileController by MissileControllerImpl() {
    val poi = type.poi()
    var shield: ShieldRenderer? = null
    val opposite: GameTeam get() = game.teams.first { it != this }
    val node = EventNode.type("game-team-$type", EventFilter.PLAYER) { _, player -> players.contains(player) }
        .listen { event: PlayerMoveEvent ->
            if (event.newPosition.y < poi.minY) {
                event.player.teleport(poi.spawn)
                event.player.pling()
            }
        }

    init {
        poi.shieldGenerator.team = this
        poi.map.team = this

        setSelf(this)

        val pipesAtStart = 32
        val pipesPerPlayer = pipesAtStart / players.size.coerceAtLeast(1)
        players.forEach {
            tabListExtras[it] = type.short + " "
            it.inventory.let { inventory ->
                inventory.clear()
                inventory.addItemStack(HammerItem.createItem())
                inventory.addItemStack(PipeBlockItem.createNewStack(pipesPerPlayer))
            }
        }
    }

    private fun spawnShield(instance: Instance) {
        shield = ShieldRenderer(instance, poi.mainShield.toVertices())
        poi.shieldGenerator.apply {
            setInstance(instance)
            setGroup(this@GameTeam)
            registerRefillEvent(node)
        }
    }

    private fun initOilBiome(instance: Instance) {
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

    fun init(instance: Instance) {
        spawnShield(instance)
        initOilBiome(instance)
        poi.map.setInstance(instance)
        poi.nodes.forEach { it.setInstance(instance) }
        poi.shops.forEach { it.setInstance(instance) }
    }
}