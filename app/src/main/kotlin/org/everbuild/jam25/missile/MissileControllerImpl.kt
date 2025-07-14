package org.everbuild.jam25.missile

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance

class MissileControllerImpl : MissileController {
    override val missileTracker = mutableListOf<Missile>()

    override fun spawnMissile(pos: BlockVec, instance: Instance, missile: Missile) {
        missile.setInstance(instance, pos)
        missileTracker.add(missile)
    }
}