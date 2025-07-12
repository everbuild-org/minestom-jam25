package org.everbuild.jam25.world.shield

import net.minestom.server.coordinate.Pos
import org.joml.Vector3f

data class Shield(
    val parts: List<List<Pos>>,
) {
    fun toVertices() = parts.map { it.map { p -> Vector3f(p.x.toFloat(), p.y.toFloat(), p.z.toFloat()) } }
}
