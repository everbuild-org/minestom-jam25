package org.everbuild.jam25.world.shield.generator

import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.BlockDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

class PowerBarRenderer(instance: Instance, pos: Pos) : AutoCloseable {
    companion object {
        private const val POWER_BAR_WIDTH = 1.375
        private const val POWER_BAR_HEIGHT = 0.1875
    }

    val hullEntity: Entity = createStretchedBlockEntity(instance, pos, POWER_BAR_WIDTH + 0.125, POWER_BAR_HEIGHT + 0.125, 0.125, Block.WHITE_CONCRETE)
    val barEntity: Entity = createStretchedBlockEntity(instance, pos, POWER_BAR_WIDTH, POWER_BAR_HEIGHT, 0.15, Block.LIME_CONCRETE)
    val pendingRepairsEntity: Entity = createStretchedBlockEntity(instance, pos, POWER_BAR_WIDTH, POWER_BAR_HEIGHT, 0.14, Block.LIME_STAINED_GLASS)

    private fun createStretchedBlockEntity(instance: Instance, center: Pos, width: Double, height: Double, depth: Double, block: Block) = Entity(EntityType.BLOCK_DISPLAY).apply {
        editEntityMeta(BlockDisplayMeta::class.java) {
            it.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.FIXED
            it.isHasNoGravity = true
            it.brightnessOverride = 15
            it.setBrightness(15, 15)
            it.setBlockState(block)
            it.scale = Vec(width, height, depth)
            it.translation = Vec(-width / 2, -height / 2, -depth / 2)
        }
        setInstance(instance, center)
    }

    fun update(powerLevel: Double, pendingRefill: Double) {
        barEntity.editEntityMeta(BlockDisplayMeta::class.java) {
            it.scale = it.scale.withX(POWER_BAR_WIDTH * powerLevel / 100.0)
        }
        pendingRepairsEntity.editEntityMeta(BlockDisplayMeta::class.java) {
            if (pendingRefill == 0.0) {
                if (it.scale.x() != 0.0) it.scale = it.scale.withX(0.0)
            } else {
                it.scale = it.scale.withX(POWER_BAR_WIDTH * (powerLevel + pendingRefill).coerceAtMost(100.0) / 100.0)
            }
        }
    }

    override fun close() {
        hullEntity.remove()
        barEntity.remove()
        pendingRepairsEntity.remove()
    }
}