package org.everbuild.jam25.world.shield

import kotlin.math.abs
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.jam25.util.MatrixStack
import org.joml.Matrix4f
import org.joml.Vector3f

class QuadRenderer(
    val instance: Instance,
    val vertices: List<Vector3f>,
) : AutoCloseable {
    val entity = Entity(EntityType.TEXT_DISPLAY)
    val spawn = Pos(6.0, 11.25, 67.0)

    init {
        entity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = " ".minimessage()
            meta.viewRange = 2f
            meta.backgroundColor = 0xFE0000

            val stack = MatrixStack()

            stack.translate(-spawn.x.toFloat(), -spawn.y.toFloat(), -spawn.z.toFloat())

            applyToVertices(stack)

            // correct for origin
            stack.translate(-0.0989f, -0.4958f, 0.0f)

            // correct for form
            stack.scale(SCALE_BLOCK_X, SCALE_BLOCK_Y, 1f)
            val decomp = stack.decompose()
            decomp.apply(meta)
        }

        entity.setNoGravity(true)

        entity.setInstance(instance, spawn)
    }

    fun applyToVertices(stack: MatrixStack) {
        val p0 = vertices[0]
        val p1 = vertices[1]
        val p3 = vertices[3]

        val widthVector = p1.sub(p0, Vector3f())
        val heightVector = p3.sub(p0, Vector3f())

        if (abs(widthVector.dot(heightVector)) > 0.001f) {
            println("Warning: The provided vertices do not form a perpendicular rectangle.")
        }

        val scaleX = widthVector.length()
        val scaleY = heightVector.length()

        val p2 = vertices[2]
        val center = p0.add(p2, Vector3f()).mul(0.5f)

        val targetX = widthVector.normalize(Vector3f())
        val targetY = heightVector.normalize(Vector3f())
        val targetZ = targetX.cross(targetY, Vector3f())

        val rotationMatrix = Matrix4f().set(
            targetX.x, targetY.x, targetZ.x, 0f,
            targetX.y, targetY.y, targetZ.y, 0f,
            targetX.z, targetY.z, targetZ.z, 0f,
            0f, 0f, 0f, 1f
        )

        stack.translate(center.x, center.y, center.z)
        stack.mul(rotationMatrix)
        stack.scale(scaleX, scaleY, 1f)
    }

    override fun close() {
        entity.remove()
    }

    companion object {
        const val SCALE_BLOCK_X = 7.938f
        const val SCALE_BLOCK_Y = 3.984f
    }
}