package org.everbuild.jam25.map

import net.minestom.server.coordinate.Vec
import org.joml.Vector2i

class MapMapper(var br: Vector2i, var tl: Vector2i) {
    constructor(br: Vec, tl: Vec): this(Vector2i(br.x.toInt(), br.z.toInt()), Vector2i(tl.x.toInt(), tl.z.toInt()))
    constructor(minX: Int, minY: Int, maxX: Int, maxY: Int): this(Vector2i(minX, minY), Vector2i(maxX, maxY))
    val brX = 13
    val brY = 20

    val tlX = 69
    val tlY = 94
    
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

        return Vector2i(worldX.toInt(), worldY.toInt() + 7)
    }

    /*
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

        return Vector2i(worldX.toInt(), worldY.toInt() + 7)
    }
     */
    
    fun worldToMap(x: Int, y: Int): Vector2i {
        val tlMap = Vector2i(tlX, tlY)
        val brMap = Vector2i(brX, brY)
        val tlWorld = Vector2i(tl.x, tl.y)
        val brWorld = Vector2i(br.x, br.y)

        val xRel = (x - tlWorld.x).toDouble() / (brWorld.x - tlWorld.x)
        val yRel = (y - tlWorld.y).toDouble() / (brWorld.y - tlWorld.y)

        val mapX = tlMap.x + (brMap.x - tlMap.x) * yRel
        val mapY = tlMap.y + (brMap.y - tlMap.y) * xRel

        return Vector2i(mapX.toInt(), mapY.toInt() - 6)
    }
}