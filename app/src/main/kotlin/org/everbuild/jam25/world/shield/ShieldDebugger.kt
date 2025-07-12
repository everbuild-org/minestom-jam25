package org.everbuild.jam25.world.shield

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import org.everbuild.jam25.world.GameWorld

object ShieldDebugger : Debugger {
    override val identifier: String = "shield"
    private var renderer: ShieldRenderer? = null

    @Debuggable
    fun respawnShield(player: Player) {
        renderer?.close()
        renderer = ShieldRenderer(player.instance!!, GameWorld.blueShield.toVertices())
    }
}