package org.everbuild.jam25.world

import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import org.everbuild.jam25.resource.Resource

object WorldDebugger : Debugger {
    override val identifier: String = "world"

    @Debuggable
    fun resourceNode(player: Player) {
        val pos = player.getTargetBlockPosition(4) ?: return
        val block = player.instance!!.getBlock(pos)
        val resource = when (block.defaultState()) {
            Block.OAK_PRESSURE_PLATE.defaultState() -> Resource.BIO_SCRAPS
            Block.POLISHED_BLACKSTONE_PRESSURE_PLATE.defaultState() -> Resource.SILICON_DUST
            Block.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultState() -> Resource.METAL_SCRAPS
            else -> return player.sendMessage("not a resource node")
        }

        val codegen = "ResourceNode(Pos.fromPoint(BlockVec(${pos.blockX()}, ${pos.blockY()}, ${pos.blockZ()})), Resource.${resource.name}),"
        player.sendMessage(codegen)
        println(codegen)
    }
}

