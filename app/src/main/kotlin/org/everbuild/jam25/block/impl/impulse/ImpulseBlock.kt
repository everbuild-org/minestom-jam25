package org.everbuild.jam25.block.impl.impulse

import kotlin.math.abs
import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.block.api.CustomBlock


object ImpulseBlock : CustomBlock {
    const val LAUNCH_HEIGHT: Double = 24.0
    const val JUMP_MOTION: Double = 0.42

    init {
        listen<PlayerMoveEvent> { event ->
            val player = event.player
            val oldPos = player.position
            val newPos = event.newPosition
            val below = event.instance.getBlock(newPos.withY { y -> y - 1 })
            if (below.key() != this.key())
                return@listen

            val dVec: Vec = newPos.sub(oldPos).asVec()
            val length = dVec.y()

            val velocity = player.velocity
            if (abs(length - JUMP_MOTION) < 0.0001) {
                player.velocity = velocity.withY(LAUNCH_HEIGHT)
                return@listen
            }

            if (oldPos.sameBlock(newPos)) return@listen

            if (velocity.y < -15 && !player.isOnGround) {
                val lookingDirVec = player.position.direction().normalize()
                player.velocity = velocity.withY { 0.5 }.add(lookingDirVec.mul(5.0))
            }
        }
    }

    override fun key(): Key = Block.SLIME_BLOCK.key()

    override fun placeBlock(instance: Instance, position: BlockVec, player: Player?) {
        instance.setBlock(position, Block.SLIME_BLOCK)
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: Player?) {
        instance.setBlock(position, Block.AIR)
    }

    override fun update(instance: Instance, position: BlockVec) {

    }
}