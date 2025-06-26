package org.everbuild.celestia.orion.platform.minestom.util

import net.minestom.server.coordinate.Pos
import org.everbuild.celestia.orion.core.util.FullPosition
import org.everbuild.celestia.orion.core.util.Vec3d
import org.everbuild.celestia.orion.core.util.Vec3f
import org.everbuild.celestia.orion.core.util.Vec3i

fun FullPosition.toPos(): Pos = Pos(x, y, z, yaw, pitch)
fun Vec3i.toPos(): Pos = Pos(x, y, z)
fun Vec3f.toPos(): Pos = Pos(x.toDouble(), y.toDouble(), z.toDouble())
fun Vec3d.toPos(): Pos = Pos(x, y, z)
fun Pos.toFull() = FullPosition(x, y, z, yaw, pitch)