package org.everbuild.jam25.missile

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Explosion
import net.minestom.server.instance.ExplosionSupplier
import net.minestom.server.instance.Instance
import org.everbuild.asorda.resources.data.api.lockfile.instruments
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import org.everbuild.jam25.Jam
import org.everbuild.jam25.block.impl.launcher.MissileLauncherBlock
import org.joml.Vector2i

object MissileDebugger : Debugger {
    override val identifier: String = "missile"

    @Debuggable
    fun launch(player: Player) {
        val team = Jam.gameStates.getInGamePhase(player)?.teamOf(player) ?: return
        player.sendMessage("Launching missile")
        team.missileTracker.removeFirstOrNull()?.also {
            player.sendMessage("Launching missile $it")
        }?.shoot(Vector2i(13, 5)) { pos ->
            player.sendMessage("ground reached")
            player.instance.explosionSupplier = object : ExplosionSupplier {
                override fun createExplosion(
                    centerX: Float,
                    centerY: Float,
                    centerZ: Float,
                    strength: Float,
                    additionalData: CompoundBinaryTag?
                ): Explosion? {
                    return object : Explosion(centerX, centerY, centerZ, strength) {
                        override fun prepare(instance: Instance?): List<Point> {
                            return listOf()
                        }
                    }
                }

            }
            player.instance.explode(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat(), 20f)
        }
    }

    @Debuggable
    fun assemble(player: Player) {
        MissileLauncherBlock.entities.values.flatMap { it.values }.forEach {
            it.run()
        }
    }
}