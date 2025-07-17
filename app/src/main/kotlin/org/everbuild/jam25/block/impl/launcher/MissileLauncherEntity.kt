package org.everbuild.jam25.block.impl.launcher

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.worldseed.multipart.animations.AnimationHandler
import net.worldseed.multipart.animations.AnimationHandlerImpl
import org.everbuild.celestia.orion.platform.minestom.api.utils.plus
import java.util.concurrent.CompletableFuture

class MissileLauncherEntity() : EntityCreature(EntityType.ITEM_DISPLAY) {
    val modelEntity = MissileLauncherModel()
    lateinit var anim: AnimationHandler

    override fun setInstance(instance: Instance, spawnPosition: Pos): CompletableFuture<Void?>? {
        modelEntity.init(instance, Pos.fromPoint(spawnPosition.plus(Pos(0.5, 0.0, 0.5))))
        anim = AnimationHandlerImpl(modelEntity)
        anim.playRepeat("idle")

        return super.setInstance(instance, spawnPosition)
    }

    fun run(then: () -> Unit) {
        anim.playOnce("place") {
            anim.playRepeat("idle")
            then()
        }
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
    }
}