package org.everbuild.jam25.missile

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance

interface MissileController {
    val missileTracker: MutableList<Missile>
    fun spawnMissile(pos: BlockVec, instance: Instance, missile: Missile)
}