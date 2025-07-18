package org.everbuild.jam25.world.shield.generator

import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.api.utils.rotateAroundYDegrees

class ShieldGeneratorRefillRenderer(instance: Instance, position: Vec, direction: Direction) : AutoCloseable {
    val yaw = when (direction) {
        Direction.EAST -> -90f
        Direction.WEST -> 90f
        Direction.NORTH -> 180f
        else -> 0f
    } + 90f
    val refillTooltipYaw = yaw - 90f
    private val refillTooltip: Entity = createTooltip(instance, Pos(position.add(Vec(0.0, -0.45, 1.85).rotateAroundYDegrees(-refillTooltipYaw.toDouble())), refillTooltipYaw, -55f))
    private val powerBarRenderers: List<PowerBarRenderer> = listOf(
        PowerBarRenderer(instance, Pos(position.add(Vec(-0.875, 0.59375, 0.0).rotateAroundYDegrees(-yaw.toDouble())), -90f + yaw, 0f)),
        PowerBarRenderer(instance, Pos(position.add(Vec(0.875, 0.59375, 0.0).rotateAroundYDegrees(-yaw.toDouble())), 90f + yaw, 0f)),
    )

    fun update(powerLevel: Double, pendingRefill: Double) {
        powerBarRenderers.forEach { it.update(powerLevel, pendingRefill) }
    }

    private fun createTooltip(instance: Instance, pos: Pos) = Entity(EntityType.TEXT_DISPLAY).apply {
        editEntityMeta(TextDisplayMeta::class.java) {
            it.alignment = TextDisplayMeta.Alignment.CENTER
            it.text = "<red>Right click with <green>Bio Scraps<red><newline>to refill".minimessage()
            it.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.FIXED
            it.isHasNoGravity = true
        }
        setInstance(instance, pos)
    }

    override fun close() {
        refillTooltip.remove()
        powerBarRenderers.forEach(AutoCloseable::close)
    }
}