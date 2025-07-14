package org.everbuild.jam25.missile

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.joml.Vector2i

class MissileControllerImpl : MissileController {
    override val missileTracker = mutableListOf<Missile>()
    override val targetPositions = mutableListOf<Vector2i>()

    override fun spawnMissile(pos: BlockVec, instance: Instance, missile: Missile) {
        missile.setInstance(instance, pos)
        missileTracker.add(missile)
    }

    override fun tryLaunch() {
        if (missileTracker.isEmpty()) return
        targetPositions.removeFirstOrNull()?.let {
            val missile = missileTracker.first()
            val result = missile.shoot(it) {
                // TODO
            }
            if (result) {
                missileTracker.remove(missile)
            }
        }
    }
}