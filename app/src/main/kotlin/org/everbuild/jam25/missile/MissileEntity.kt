package org.everbuild.jam25.missile

import java.util.concurrent.CompletableFuture
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.platform.minestom.api.utils.plus

class MissileEntity(missileType: MissileType) : EntityCreature(EntityType.ITEM_DISPLAY) {
    val model = MissileModel(missileType.num)

    init {
        setNoGravity(true)
    }

    override fun tick(time: Long) {
        super.tick(time)
        if (!this.isDead) {
            model.position = this.position
            model.setGlobalRotation(this.position.yaw.toDouble(), this.position.pitch.toDouble())
        }
    }

    override fun setInstance(instance: Instance, spawnPosition: Pos): CompletableFuture<Void?>? {
        model.init(instance, Pos.fromPoint(spawnPosition.plus(Pos(0.5, 0.0, 0.5))))

        return super.setInstance(instance, spawnPosition)
    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)
        this.model.addViewer(player)
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        this.model.removeViewer(player)
    }

    override fun remove() {
        super.remove()
        this.model.destroy()
    }
}