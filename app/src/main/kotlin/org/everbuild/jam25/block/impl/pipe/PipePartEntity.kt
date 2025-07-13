package org.everbuild.jam25.block.impl.pipe

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.worldseed.multipart.animations.AnimationHandlerImpl


class PipePartEntity : EntityCreature(EntityType.ITEM_DISPLAY) {
    val part = PipePart()
    val animationHandler = AnimationHandlerImpl(part)

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
        this.animationHandler.destroy()
    }
}