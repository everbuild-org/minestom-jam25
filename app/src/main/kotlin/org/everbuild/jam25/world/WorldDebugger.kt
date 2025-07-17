package org.everbuild.jam25.world

import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import org.everbuild.jam25.Jam
import org.everbuild.jam25.resource.SpawneableResource

object WorldDebugger : Debugger {
    override val identifier: String = "world"

    @Debuggable
    fun resourceNode(player: Player) {
        val pos = player.getTargetBlockPosition(4) ?: return
        val block = player.instance!!.getBlock(pos)
        val spawneableResource = when (block.defaultState()) {
            Block.OAK_PRESSURE_PLATE.defaultState() -> SpawneableResource.BIO_SCRAPS
            Block.POLISHED_BLACKSTONE_PRESSURE_PLATE.defaultState() -> SpawneableResource.SILICON_DUST
            Block.HEAVY_WEIGHTED_PRESSURE_PLATE.defaultState() -> SpawneableResource.METAL_SCRAPS
            else -> return player.sendMessage("not a resource node")
        }

        val codegen = "ResourceNode(Pos.fromPoint(BlockVec(${pos.blockX()}, ${pos.blockY()}, ${pos.blockZ()})), SpawneableResource.${spawneableResource.name}),"
        player.sendMessage(codegen)
        println(codegen)
    }

    @Debuggable
    fun refillGenerators(player: Player) {
        val game = Jam.gameStates.getInGamePhase(player) ?: return
        game.teams.forEach { team ->
            team.poi.shieldGenerator.refill(100.0)
        }
    }
}

