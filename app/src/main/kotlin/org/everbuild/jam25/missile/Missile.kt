package org.everbuild.jam25.missile

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.jam25.util.peeking
import org.joml.Vector2i
import kotlin.math.*

open class Missile(val entity: Entity) {
    private lateinit var instance: Instance
    var task: Task? = null

    val explosionSound = Sound.sound { it.type(Key.key("minecraft:entity.generic.explode")) }
    val launchSound = Sound.sound {
        it.type(Key.key("entity.cat.hiss"))
        it.volume(0.5f)
    }
    val flySound = Sound.sound {
        it.type(Key.key("entity.generic.extinguish_fire"))
        it.volume(0.35f)
    }
    val flySoundFrequencyTicks = 10

    fun setInstance(instance: Instance, pos: BlockVec) {
        this.instance = instance
        entity.setInstance(instance, pos.add(0.5, 0.0, 0.5))
    }

    fun generateParabolicFlightPathSequence(
        currentPos: Pos,
        targetPos: Pos,
        maxHeightOffset: Double,
        numSteps: Int
    ): Sequence<Pos> = sequence {
        require(numSteps >= 1) { "numSteps must be at least 1." }
        val dx = targetPos.x - currentPos.x
        val dz = targetPos.z - currentPos.z
        val totalHorizontalDistance = sqrt(dx.pow(2) + dz.pow(2))

        val yStart = currentPos.y
        val yEnd = targetPos.y
        val yApex = max(yStart, yEnd) + maxHeightOffset

        val c = yStart
        val a = -4.0 * (yApex - 0.5 * (yEnd + yStart))
        val b = yEnd - yStart - a

        for (i in 0 until numSteps) {
            val t = if (numSteps == 1) 0.0 else i.toDouble() / (numSteps - 1).toDouble()

            val currentX = currentPos.x + dx * t
            val currentZ = currentPos.z + dz * t
            val currentY = a * t.pow(2) + b * t + c
            val currentYaw = if (totalHorizontalDistance < 1e-6) {
                currentPos.yaw
            } else {
                atan2(dz, dx).toFloat().toDegrees() // Convert radians to degrees
            }
            val fixedYaw = fixYaw(currentYaw)

            val dyDt = 2.0 * a * t + b
            val currentPitch = if (totalHorizontalDistance < 1e-6 && abs(dyDt) < 1e-6) {
                currentPos.pitch
            } else {
                atan2(dyDt, totalHorizontalDistance).toFloat().toDegrees() // Convert radians to degrees
            }

            val newPos = Pos(currentX, currentY, currentZ, fixedYaw, currentPitch)
            yield(newPos)
        }
    }

    fun shoot(position: Vector2i, continuation: (Pos) -> Unit): Boolean {
        val maxY = (-100 until 100).reversed().firstOrNull { instance.getBlock(position.x, it, position.y).isSolid } ?: run {
            return false
        }
        val current = this.entity.position
        val target = Pos(position.x.toDouble(), maxY.toDouble(), position.y.toDouble())
        val sequence = generateParabolicFlightPathSequence(current, target, 16.0, 100).toList().iterator().peeking()
        var counter = 0
        this.entity.instance.playSound(launchSound, this.entity.position)
        task = Mc.scheduler.buildTask {
            if (!sequence.hasNext()) {
                continuation(target)
                this.entity.instance.playSound(explosionSound, this.entity.position)
                this.entity.instance.sendGroupedPacket(
                    ParticlePacket(
                        Particle.EXPLOSION,
                        false, true,
                        this.entity.position.add(0.0, 1.0, 0.0),
                        Pos.ZERO,
                        1.5f,
                        50
                    )
                )
                this.entity.instance.sendGroupedPacket(
                    ParticlePacket(
                        Particle.GUST,
                        false, true,
                        this.entity.position.add(0.0, 1.0, 0.0),
                        Pos.ZERO,
                        1.5f,
                        100
                    )
                )
                this.entity.instance.sendGroupedPacket(
                    ParticlePacket(
                        Particle.SMALL_FLAME,
                        false, true,
                        this.entity.position.add(0.0, 1.0, 0.0),
                        Pos.ZERO,
                        0.5f,
                        100
                    )
                )
                task!!.cancel()
                this.entity.remove()
                return@buildTask
            }
            val nextPos = sequence.next()
            if (!sequence.hasNext()) {
                this.entity.teleport(nextPos)
                return@buildTask
            }
            val positionAfterThat = sequence.peek()
            val dir = nextPos.withLookAt(positionAfterThat).direction()
            val pos = positionAfterThat.withDirection(dir)
            counter++
            if (counter < 25) {
                this.entity.teleport(pos)
            } else {
                this.entity.teleport(pos.withYaw(fixYaw(pos.yaw + 180)).withPitch(90f))
            }
            if (counter % 3 == 0) {
                this.entity.instance.sendGroupedPacket(
                    ParticlePacket(
                        Particle.CLOUD,
                        false, true,
                        this.entity.position,
                        Pos.ZERO,
                        0f,
                        3
                    )
                )
                this.entity.instance.sendGroupedPacket(
                    ParticlePacket(
                        Particle.FLAME,
                        false, true,
                        this.entity.position,
                        Pos.ZERO,
                        0f,
                        3
                    )
                )
            }
            if(counter % flySoundFrequencyTicks == 0) {
                this.entity.instance.playSound(flySound, this.entity.position)
            }
        }.repeat(TaskSchedule.nextTick()).schedule()

        return true
    }

    fun fixYaw(yaw: Float): Float {
        var fixedYaw = yaw % 360.0f
        if (fixedYaw < 0) {
            fixedYaw += 360.0f
        }
        return fixedYaw
    }

    fun Float.toDegrees(): Float {
        return this * (180f / PI.toFloat())
    }

    fun remove() {
        entity.remove()
        task?.cancel()
    }
}