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

class InteractionController(base: Pos, dir: Vec, instance: Instance, w: Int = 1, h: Int = 1, r: Double = 0.1, val inv: Boolean, val cb: (Int, Int, Pos) -> Unit) {
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
                it.setTag(sih, scaledIHat)
                it.setTag(sjh, scaledJHat)
                it.setTag(controller, this)
                it.setTag(invTag, inv)
            }
        }
    }

    fun remove() {
        entities.forEach { it.remove() }
    }

    companion object {
        val xTag = Tag.Integer("px")
        val yTag = Tag.Integer("py")
        val sih = Tag.Transient<Vec>("sih")
        val sjh = Tag.Transient<Vec>("sjh")
        val invTag = Tag.Boolean("inv")
        val controller = Tag.Transient<InteractionController>("ctrl")

        fun eventNode() = EventNode.all("map")
            .listen { event: PlayerEntityInteractEvent ->
                val entity = event.target
                if (!entity.hasTag(xTag)) return@listen
                if (!entity.hasTag(yTag)) return@listen
                if (!entity.hasTag(controller)) return@listen
                val x = entity.getTag(xTag)
                val y = entity.getTag(yTag)
                val iHat = entity.getTag(sih)
                val jHat = entity.getTag(sjh)
                val inv = entity.getTag(invTag)
                val controller = entity.getTag(controller)
                val subpixel = event.interactPosition
                val dx = (if(subpixel.x() < 0.0) 1 else 0).let { if (inv) 1-it else it}
                val dy = (if(subpixel.y() > 0.02) 1 else 0).let { if (inv) it else it}
                controller.cb(x * 2 + dx, y * 2 + dy, entity.position.add(iHat.mul(0.5 * dx)).add(jHat.mul(0.5 * dy)))
            }
    }
}