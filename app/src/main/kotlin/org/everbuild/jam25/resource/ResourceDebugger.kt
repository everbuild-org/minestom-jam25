package org.everbuild.jam25.resource

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.api.utils.roundToBlock
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import org.everbuild.jam25.Jam

object ResourceDebugger : Debugger {
    override val identifier: String = "resource"

    @Debuggable
    fun spawnBio(player: Player) = Jam.gameStates.getInGamePhase(player)?.createResourceNode(Resource.BIO_SCRAPS, player.position.roundToBlock())
    @Debuggable
    fun spawnMetal(player: Player) = Jam.gameStates.getInGamePhase(player)?.createResourceNode(Resource.METAL_SCRAPS, player.position.roundToBlock())
    @Debuggable
    fun spawnSi(player: Player) = Jam.gameStates.getInGamePhase(player)?.createResourceNode(Resource.SILICON_DUST, player.position.roundToBlock())
}