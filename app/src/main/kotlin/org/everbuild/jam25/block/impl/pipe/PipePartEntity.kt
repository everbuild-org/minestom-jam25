package org.everbuild.jam25.block.impl.pipe

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.BlockFace


class PipePartEntity(type: String, face: BlockFace?) : EntityCreature(EntityType.FROG) {
    val part = PipePart(type, face)

    init {
        println("$type $face ${part.parts}")
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