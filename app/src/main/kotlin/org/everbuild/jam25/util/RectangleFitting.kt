package org.everbuild.jam25.util

import kotlin.math.abs
import org.joml.Vector3f


data class RectangleScales(val width: Float, val height: Float, val plane: String)

fun getAxisAlignedRectangleScales(minXYZ: Vector3f, maxXYZ: Vector3f, tolerance: Float = 0.001f): RectangleScales? {
    val dimensions = maxXYZ.sub(minXYZ, Vector3f())
    return when {
        abs(dimensions.z) < tolerance -> RectangleScales(dimensions.x, dimensions.y, "XY")
        abs(dimensions.y) < tolerance -> RectangleScales(dimensions.x, dimensions.z, "XZ")
        abs(dimensions.x) < tolerance -> RectangleScales(dimensions.y, dimensions.z, "YZ")
        else -> null
    }
}