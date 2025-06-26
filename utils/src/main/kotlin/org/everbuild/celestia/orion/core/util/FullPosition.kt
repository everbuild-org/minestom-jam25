package org.everbuild.celestia.orion.core.util

data class FullPosition(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float)

fun Vec3i.toNulledFull(): FullPosition = FullPosition(x.toDouble(), y.toDouble(), z.toDouble(), 0f, 0f)
fun Vec3d.toNulledFull(): FullPosition = FullPosition(x, y, z, 0f, 0f)
fun Vec3f.toNulledFull(): FullPosition = FullPosition(x.toDouble(), y.toDouble(), z.toDouble(), 0f, 0f)
