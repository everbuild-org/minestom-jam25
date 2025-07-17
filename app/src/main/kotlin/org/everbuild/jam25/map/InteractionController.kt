package org.everbuild.jam25.map

import kotlin.math.cos
import kotlin.math.sin
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.instance.Instance

class InteractionController(base: Pos, instance: Instance, w: Int = 1, h: Int = 1, r: Double = 0.1) {
    val scaledIHat = base.dir().rotateAroundY(-90.0)
    val scaledJHat = base.withPitch(-90f).dir()
    val discreteWSteps = (w / r).toInt()
    val discreteHSteps = (h / r).toInt()

    init {
        println(base)
        println(base.dir())
    }

    val entities = (0..discreteWSteps).flatMap { wStep ->
        (0..discreteHSteps).map { hStep ->
            Entity(EntityType.INTERACTION).also {
                it.setInstance(instance, base.add(scaledIHat.mul(wStep * r)).add(scaledJHat.mul(hStep * r)))
                it.editEntityMeta(InteractionMeta::class.java) { meta ->
                    meta.width = r.toFloat()
                    meta.height = r.toFloat()
                    meta.response = true
                }
            }
        }
    }

    fun Pos.dir(): Vec {
        return dirOf(this.yaw, this.pitch)
    }


    fun dirOf(yaw: Float, pitch: Float): Vec {
        val yawRad = Math.toRadians(yaw.toDouble())
        val pitchRad = Math.toRadians(pitch.toDouble())

        val x = cos(yawRad) * cos(pitchRad)
        val y = sin(pitchRad)
        val z = sin(yawRad) * cos(pitchRad)

        return Vec(x, y, z)
    }


    fun remove() {
        entities.forEach { it.remove() }
    }
}