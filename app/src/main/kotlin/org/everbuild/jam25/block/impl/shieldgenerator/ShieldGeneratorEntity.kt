package org.everbuild.jam25.block.impl.shieldgenerator

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Direction
import net.worldseed.multipart.animations.AnimationHandler
import net.worldseed.multipart.animations.AnimationHandlerImpl
import org.everbuild.celestia.orion.platform.minestom.api.utils.plus
import java.util.concurrent.CompletableFuture

class ShieldGeneratorEntity(val running: Boolean, direction: Direction) : EntityCreature(EntityType.ITEM_DISPLAY) {
    val modelEntity = ShieldGeneratorModel()
    var animationHandler: AnimationHandler? = null
    val yaw = when (direction) {
        Direction.EAST -> -90f
        Direction.WEST -> 90f
        Direction.NORTH -> 180f
        else -> 0f
    } + 90f

    override fun setInstance(instance: Instance, spawnPosition: Pos): CompletableFuture<Void?>? {
        modelEntity.init(instance, Pos.fromPoint(spawnPosition.plus(Pos(0.5, 0.0, 0.5))).withYaw(yaw))
        animationHandler = AnimationHandlerImpl(modelEntity)
        animationHandler?.playRepeat(if (running) "running" else "idle")

        return super.setInstance(instance, spawnPosition)
    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)
        this.modelEntity.addViewer(player)
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        this.modelEntity.removeViewer(player)
    }

    override fun remove() {
        super.remove()
        this.modelEntity.destroy()
        this.animationHandler?.destroy()
    }
}