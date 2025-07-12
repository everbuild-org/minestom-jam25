package org.everbuild.jam25.world

import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.Point

data class FlatArea(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int,
) {
    constructor(box: BoundingBox) : this(box.minX().toInt(), box.minY().toInt(), box.maxX().toInt(), box.maxY().toInt())

    fun contains(x: Int, y: Int) = x in minX..maxX && y in minY..maxY
    fun contains(area: FlatArea) = area.minX >= minX && area.maxX <= maxX && area.minY >= minY && area.maxY <= maxY
    fun contains(point: Point) = contains(point.x().toInt(), point.y().toInt())
    fun contains(box: BoundingBox) = contains(FlatArea(box))
}