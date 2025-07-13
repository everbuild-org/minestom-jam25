package org.everbuild.jam25.world

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import org.everbuild.jam25.Jam

object WorldDebugger : Debugger {
    override val identifier: String = "world"

    @Debuggable
    fun shoot(player: Player) {
        val phase = Jam.gameStates.getInGamePhase(player) ?: return
        val instance = phase.world.instance
//        phase.teamOf(player)?.poi?.turret?.consumeEvent(instance, TurretEvent.Fire)
    }

    @Debuggable
    fun progress(player: Player) {
        val phase = Jam.gameStates.getInGamePhase(player) ?: return
        val instance = phase.world.instance
//        phase.teamOf(player)?.poi?.turret?.consumeEvent(instance, TurretEvent.Progress)
    }
}

