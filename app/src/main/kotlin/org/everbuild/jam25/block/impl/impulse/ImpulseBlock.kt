package org.everbuild.jam25.block.impl.impulse

import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.block.api.CustomBlock

const val LAUNCH_HEIGHT: Double = 24.0

object ImpulseBlock : CustomBlock {

    init {
        listen<PlayerMoveEvent> { event ->
            val player = event.player
            val oldPos = player.position
            val newPos = event.newPosition
            val below = event.instance.getBlock(newPos.withY { y -> y - 1 })
            if (below.key() != this.key() || oldPos.sameBlock(newPos))
                return@listen

            val velocity = player.velocity
            player.velocity = velocity.withY {
                if (velocity.y < 0 && !event.player.isOnGround) 0.001 else LAUNCH_HEIGHT
            }
        }
    }

    override fun key(): Key = Key.key("jam", "impulse")

    override fun placeBlock(instance: Instance, position: BlockVec, player: Player) {
        instance.setBlock(position, Block.SLIME_BLOCK)
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: Player) {
        instance.setBlock(position, Block.AIR)
    }

    override fun update(instance: Instance, position: BlockVec) {

    }
}