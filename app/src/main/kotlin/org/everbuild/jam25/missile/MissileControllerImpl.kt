package org.everbuild.jam25.missile

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.ingame.GameTeam
import org.joml.Vector2i

class MissileControllerImpl : MissileController {
    override val missileTracker = mutableListOf<Missile>()
    override val targetPositions = mutableListOf<Vector2i>()
    private lateinit var self: GameTeam
    private var lastTickNoPos = true

    override fun spawnMissile(pos: BlockVec, instance: Instance, missile: Missile) {
        missile.setInstance(instance, pos)
        missileTracker.add(missile)
    }

    override fun tryLaunch() {
        if (missileTracker.isEmpty()) return

        if (targetPositions.isEmpty()) {
            if (lastTickNoPos) {
                self.sendMiniMessage("${Jam.PREFIX} <red>No target positions set! Missiles will not launch until one is set in the war room.")
                lastTickNoPos = false
            }
        } else {
            lastTickNoPos = true
        }

        while (targetPositions.isNotEmpty()) {
            targetPositions.removeFirstOrNull()?.let {
                val missile = missileTracker.first()
                val result = missile.shoot(it) {
                    // TODO
                    println("boom")
                }
                if (result) {
                    self.poi.map.removeX(it)
                    missileTracker.remove(missile)
                    return@tryLaunch
                }
            }
        }
    }

    override fun setSelf(team: GameTeam) {
        self = team
    }
}