package org.everbuild.jam25.block.impl.pipe

import java.util.concurrent.CompletableFuture
import kotlin.io.path.exists
import kotlin.time.Duration.Companion.seconds
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.BlockFace
import org.everbuild.celestia.orion.platform.minestom.api.utils.plus

class PipePartEntity(type: String, face: BlockFace?) : EntityCreature(EntityType.ITEM_DISPLAY) {
    val part = PipePart(type, face)

    override fun setInstance(instance: Instance, spawnPosition: Pos): CompletableFuture<Void?>? {
        part.init(instance, Pos.fromPoint(spawnPosition.plus(Pos(0.5, 0.0, 0.5))))

        return super.setInstance(instance, spawnPosition)
    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)
        this.part.addViewer(player)
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        this.part.removeViewer(player)
    }

    override fun remove() {
        super.remove()
        this.part.destroy()
    }
}