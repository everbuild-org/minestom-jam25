package org.everbuild.jam25.missile

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
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

open class Missile(val entity: Entity) {
    private lateinit var instance: Instance
    fun setInstance(instance: Instance, pos: BlockVec) {
        this.instance = instance
        entity.setInstance(instance, pos)
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

        // 2. Determine Parabola Parameters for Vertical (Y-axis) Movement
        val yStart = currentPos.y
        val yEnd = targetPos.y
        val yApex = max(yStart, yEnd) + maxHeightOffset

        // Solve for A, B, C in y(t) = A * t^2 + B * t + C
        val C = yStart
        // A = -4 * (y_apex - 0.5 * (y_end + y_start))
        val A = -4.0 * (yApex - 0.5 * (yEnd + yStart))
        // B = y_end - y_start - A
        val B = yEnd - yStart - A

        // 3. Generate Path Points in a Loop
        for (i in 0 until numSteps) {
            // Calculate Normalized Progress (t)
            val t = if (numSteps == 1) 0.0 else i.toDouble() / (numSteps - 1).toDouble()

            // Calculate Current Position (x, y, z)
            val currentX = currentPos.x + dx * t
            val currentZ = currentPos.z + dz * t
            val currentY = A * t.pow(2) + B * t + C

            // Calculate Current Orientation (Yaw and Pitch)
            val currentYaw: Float
            if (totalHorizontalDistance < 1e-6) { // Check for very small distance to avoid division by zero/NaN
                currentYaw = currentPos.yaw // If no horizontal movement, maintain initial yaw
            } else {
                // atan2(y, x) calculates angle from positive X-axis to point (x, y)
                // Here, dz is like 'y' and dx is like 'x' for the horizontal plane.
                currentYaw = atan2(dz, dx).toFloat().toDegrees() // Convert radians to degrees
                // Apply Pos.fixYaw to ensure it's in the correct range
                // Note: atan2 returns radians, so convert to degrees if your Pos.yaw expects degrees
                // and then fix it. If Pos.yaw expects radians, skip toDegrees().
            }
            // Ensure yaw is fixed to the desired range (e.g., 0-360)
            val fixedYaw = fixYaw(currentYaw)

            // Pitch: determined by the instantaneous slope of the parabola
            val dy_dt = 2.0 * A * t + B // Derivative of y(t) with respect to t

            val currentPitch: Float
            if (totalHorizontalDistance < 1e-6 && abs(dy_dt) < 1e-6) {
                // If no horizontal or vertical movement, maintain initial pitch
                currentPitch = currentPos.pitch
            } else {
                // atan2(y, x) where y is vertical change and x is horizontal change
                currentPitch = atan2(dy_dt, totalHorizontalDistance).toFloat().toDegrees() // Convert radians to degrees
            }

            // Create and Add Pos Object
            val newPos = Pos(currentX, currentY, currentZ, fixedYaw, currentPitch)
            yield(newPos) // Yield the current Pos object
        }
    }

    fun shoot(position: Vector2i, continuation: (Pos) -> Unit) {
        val maxY = (-100 until 100).reversed().firstOrNull { instance.getBlock(position.x, it, position.y).isSolid } ?: run {
            println("No solid block found above $position")
            return
        }
        val current = this.entity.position
        val target = Pos(position.x.toDouble(), maxY.toDouble(), position.y.toDouble())
        val sequence = generateParabolicFlightPathSequence(current, target, 16.0, 100).toList().iterator().peeking()
        var task: Task? = null
        var counter = 0
        task = Mc.scheduler.buildTask{
            if (!sequence.hasNext()) {
                continuation(target)
                this.entity.instance.sendGroupedPacket(ParticlePacket(
                    Particle.EXPLOSION,
                    false, true,
                    this.entity.position.add(0.0, 1.0, 0.0),
                    Pos.ZERO,
                    1.5f,
                    50
                ))
                this.entity.instance.sendGroupedPacket(ParticlePacket(
                    Particle.GUST,
                    false, true,
                    this.entity.position.add(0.0, 1.0, 0.0),
                    Pos.ZERO,
                    1.5f,
                    100
                ))
                this.entity.instance.sendGroupedPacket(ParticlePacket(
                    Particle.SMALL_FLAME,
                    false, true,
                    this.entity.position.add(0.0, 1.0, 0.0),
                    Pos.ZERO,
                    0.5f,
                    100
                ))
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
                this.entity.instance.sendGroupedPacket(ParticlePacket(
                    Particle.CLOUD,
                    false, true,
                    this.entity.position,
                    Pos.ZERO,
                    0f,
                    3
                ))
                this.entity.instance.sendGroupedPacket(ParticlePacket(
                    Particle.FLAME,
                    false, true,
                    this.entity.position,
                    Pos.ZERO,
                    0f,
                    3
                ))
            }
        }.repeat(TaskSchedule.nextTick()).schedule()
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
}