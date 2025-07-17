package org.everbuild.jam25.block.impl.crafting

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.platform.minestom.api.utils.plus
import java.util.concurrent.CompletableFuture
import net.worldseed.multipart.animations.AnimationHandler
import net.worldseed.multipart.animations.AnimationHandlerImpl

class CrafterEntity(modelId: String?, facing: Direction) : EntityCreature(EntityType.ITEM_DISPLAY) {
    val modelEntity = CrafterModel(modelId)
    lateinit var anim: AnimationHandler
    val yaw = when (facing) {
        Direction.EAST -> -90f
        Direction.WEST -> 90f
        Direction.NORTH -> 180f
        else -> 0f
    }

    override fun setInstance(instance: Instance, spawnPosition: Pos): CompletableFuture<Void?>? {
        modelEntity.init(instance, Pos.fromPoint(spawnPosition.plus(Pos(0.5, 0.0, 0.5))).withYaw(yaw))
        anim = AnimationHandlerImpl(modelEntity)
        anim.playOnce("idle") {}

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

    fun craft(then: () -> Unit) {
        anim.playOnce("new") {
            then()
            anim.playOnce("idle") { }
        }
    }

    override fun remove() {
        super.remove()
        this.modelEntity.destroy()
    }
}