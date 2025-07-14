package org.everbuild.jam25.world

import kotlin.math.floor
import org.joml.Vector2i

data class Polygon(var points: List<Vector2i>) {
    constructor(vararg points: Vector2i) : this(points.toList())

    fun containsPoint(point: Vector2i): Boolean {
        if (points.isEmpty()) return false

        val px = point.x.toDouble()
        val pz = point.y.toDouble()

        var inside = false
        var i = 0
        var j = points.size - 1
        while (i < points.size) {
            val p1x = points[i].x.toDouble()
            val p1z = points[i].y.toDouble()
            val p2x = points[j].x.toDouble()
            val p2z = points[j].y.toDouble()

            if (((p1z <= pz && pz < p2z) || (p2z <= pz && pz < p1z)) &&
                (px < (p2x - p1x) * (pz - p1z) / (p2z - p1z) + p1x)
            ) {
                inside = !inside
            }
            j = i++
        }
        return inside
    }



    fun forEachVSliceInChunk(block: (x: Int, z: Int, containingVSlices: List<Vector2i>) -> Unit) {
        if (points.isEmpty()) {
            return
        }

        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minZ = points.minOf { it.y }
        val maxZ = points.maxOf { it.y }

        val minChunkX = floor(minX / 16.0).toInt()
        val maxChunkX = floor(maxX / 16.0).toInt()
        val minChunkZ = floor(minZ / 16.0).toInt()
        val maxChunkZ = floor(maxZ / 16.0).toInt()

        for (chunkX in minChunkX..maxChunkX) {
            for (chunkZ in minChunkZ..maxChunkZ) {
                val vslicesInCurrentChunkAndPolygon = mutableListOf<Vector2i>()

                for (localX in 0 until 16) {
                    for (localZ in 0 until 16) {
                        val globalVsliceX = chunkX * 16 + localX
                        val globalVsliceZ = chunkZ * 16 + localZ

                        val vslicePoint = Vector2i(globalVsliceX, globalVsliceZ)

                        if (containsPoint(vslicePoint)) {
                            vslicesInCurrentChunkAndPolygon.add(Vector2i(localX, localZ))
                        }
                    }
                }

                if (vslicesInCurrentChunkAndPolygon.isNotEmpty()) {
                    block(chunkX, chunkZ, vslicesInCurrentChunkAndPolygon)
                }
            }
        }
    }
}