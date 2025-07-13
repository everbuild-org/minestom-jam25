package org.everbuild.jam25.state.ingame

import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.platform.minestom.scoreboard.tabListExtras
import org.everbuild.jam25.DynamicGroup
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
}