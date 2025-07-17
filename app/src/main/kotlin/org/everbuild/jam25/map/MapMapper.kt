package org.everbuild.jam25.map

import net.minestom.server.coordinate.Vec
import org.joml.Vector2i

class MapMapper(val br: Vector2i, val tl: Vector2i) {
    constructor(br: Vec, tl: Vec): this(Vector2i(br.x.toInt(), br.z.toInt()), Vector2i(tl.x.toInt(), tl.z.toInt()))
    val brX = 3 * 2
    val brY = 5 * 2

    val tlX = 16 * 2
    val tlY = 22 * 2
    
    fun mapToWorld(x: Int, y: Int): Vector2i {
        val tlMap = Vector2i(tlX, tlY)
        val brMap = Vector2i(brX, brY)
        val tlWorld = Vector2i(tl.x, tl.y)
        val brWorld = Vector2i(br.x, br.y)

        val yRel = (x - tlMap.x).toDouble() / (brMap.x - tlMap.x)
        val xRel = (y - tlMap.y).toDouble() / (brMap.y - tlMap.y)

        // tlWorld and brWorld are the corresponding game world coordinates
        val worldX = tlWorld.x + (brWorld.x - tlWorld.x) * xRel
        val worldY = tlWorld.y + (brWorld.y - tlWorld.y) * yRel

        return Vector2i(worldX.toInt(), worldY.toInt())
    }
}