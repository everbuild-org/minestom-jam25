package org.everbuild.jam25.map

import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import org.everbuild.celestia.orion.platform.minestom.util.listen

class InteractionController(base: Pos, dir: Vec, instance: Instance, w: Int = 1, h: Int = 1, r: Double = 0.1, val cb: (Int, Int, Pos) -> Unit) {
    val scaledIHat = dir.normalize().mul(r)
    val scaledJHat = Vec(0.0, 1.0, 0.0).mul(r)
    val discreteWSteps = (w / r).toInt()
    val discreteHSteps = (h / r).toInt()

    val entities = (0..discreteWSteps).flatMap { wStep ->
        (0..discreteHSteps).map { hStep ->
            Entity(EntityType.INTERACTION).also {
                it.setNoGravity(true)
                it.setInstance(
                    instance,
                    base.add(scaledIHat.mul(wStep.toDouble())).add(scaledJHat.mul(hStep.toDouble()))
                )
                it.editEntityMeta(InteractionMeta::class.java) { meta ->
                    meta.width = r.toFloat()
                    meta.height = r.toFloat()
                    meta.response = true
                }
                it.setTag(xTag, wStep)
                it.setTag(yTag, hStep)
                it.setTag(controller, this)
            }
        }
    }

    fun remove() {
        entities.forEach { it.remove() }
    }

    companion object {
        val xTag = Tag.Integer("px")
        val yTag = Tag.Integer("py")
        val controller = Tag.Transient<InteractionController>("ctrl")

        fun eventNode() = EventNode.all("map")
            .listen { event: PlayerEntityInteractEvent ->
                val entity = event.target
                if (!entity.hasTag(xTag)) return@listen
                if (!entity.hasTag(yTag)) return@listen
                if (!entity.hasTag(controller)) return@listen
                val x = entity.getTag(xTag)
                val y = entity.getTag(yTag)
                val controller = entity.getTag(controller)
                controller.cb(x, y, entity.position)
            }
    }
}