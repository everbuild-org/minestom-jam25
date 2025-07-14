package org.everbuild.jam25.missile

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.joml.Vector2i

interface MissileController {
    val missileTracker: MutableList<Missile>
    val targetPositions: MutableList<Vector2i>
    fun spawnMissile(pos: BlockVec, instance: Instance, missile: Missile)

    fun tryLaunch()
}