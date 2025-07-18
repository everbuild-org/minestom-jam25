package org.everbuild.jam25.missile

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import org.everbuild.celestia.orion.platform.minestom.api.utils.playSound
import org.everbuild.jam25.Jam
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.state.ingame.GameTeam
import org.joml.Vector2i

class MissileControllerImpl : MissileController {
    override val missileTracker = mutableListOf<Missile>()
    override val targetPositions = mutableListOf<Vector2i>()
    private lateinit var self: GameTeam
    private var lastTickNoPos = true

    val boomSound = Sound.sound {
        it.type(Key.key("entity.generic.explode"))
        it.pitch(0.7f)
        it.volume(0.25f)
    }

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
                if (self.opposite.poi.mainShield.bounds().contains(it.x, it.y)) {
                    println("Shield blocked missile launch at $it")
                    return@let
                }
                val missile = missileTracker.first()
                val result = missile.shoot(it) { pos ->
                    val instance = self.game.world.instance
                    val pipes = self.game.networkController
                    val checkedBlocks = mutableListOf<BlockVec>()
                    val toDestroy = mutableListOf<BlockVec>()
                    var explosionScore = 1
                    var genHit = 0
                    val opposingMissiles = self.opposite.missileTracker.filter { v -> v.entity.instance == instance && v.entity.position.distanceSquared(pos) < 4.0 * 4.0 }
                    if (opposingMissiles.isNotEmpty()) {
                        opposingMissiles.forEach {
                            boom(instance, BlockVec(it.entity.position), 3)
                            it.remove()
                            self.opposite.missileTracker.remove(it)
                        }
                    }
                    for (dx in -4..4) for (dy in -4..4) for (dz in -4..4) {
                        val pos = BlockVec(pos.blockX() + dx, pos.blockY() + dy, pos.blockZ() + dz)
                        val block = instance.getBlock(pos)
                        if (!block.isAir)
                            if (block.getTag(BlockController.typeTag) == "jam:pipe") {
                                if (pipes.isItem(pos)) explosionScore++
                                checkedBlocks.add(pos)
                            } else if (BlockController.getBlock(block) != null && !block.getTag(BlockController.unbreakable)) {
                                toDestroy.add(pos)
                            }
                    }

                    if (pos.distance(self.opposite.poi.spawn) < 8 && self.opposite.homeBase.enabled) {
                        self.opposite.homeBase.damage(explosionScore * 2)
                        instance.sendGroupedPacket(
                            ParticlePacket(
                                Particle.EXPLOSION,
                                false, true,
                                self.opposite.poi.spawn.add(0.0, 1.0, 0.0),
                                Pos.ZERO,
                                1.5f,
                                50
                            )
                        )
                    }

                    toDestroy.forEach { block -> boom(instance, block, explosionScore) }
                    val toVisit = mutableListOf<BlockVec>().also { it.addAll(checkedBlocks) }
                    val visited = mutableListOf<BlockVec>()
                    while (toVisit.isNotEmpty()) {
                        val block = toVisit.removeFirst()
                        val neighbours = pipes.neighbouringPipes(block, instance)
                        boom(instance, block, explosionScore)
                        visited.add(block)
                        genHit += tryHitGenerator(block, instance, explosionScore)
                        if (neighbours.isEmpty()) continue
                        neighbours.forEach {
                            if (!visited.contains(it) && !toVisit.contains(it)) {
                                toVisit.add(it)
                            }
                        }
                    }

                    if (genHit > 0) {
                        self.sendMiniMessage("${Jam.PREFIX} <green>Shield generator hit for $genHit damage!")
                        println("Shield generator hit for $genHit damage!")
                    }
                }
                if (result) {
                    self.poi.map.removeX(it)
                    missileTracker.remove(missile)
                    return@tryLaunch
                }
            }
        }
    }

    fun boom(instance: Instance, block: BlockVec, explosionScore: Int) {
        instance.playSound(boomSound, block)
        instance.sendGroupedPacket(
            ParticlePacket(
                Particle.EXPLOSION,
                false, true,
                block.add(0.0, 1.0, 0.0),
                Pos.ZERO,
                1.5f,
                50
            )
        )

        BlockController.breakBlock(instance, block)
    }

    override fun setSelf(team: GameTeam) {
        self = team
    }

    private fun tryHitGenerator(
        block: BlockVec,
        instance: InstanceContainer,
        explosionScore: Int
    ): Int {
        for (dx in -2..2) for (dy in -2..2) for (dz in -2..2) {
            val blockPos = BlockVec(block.blockX() + dx, block.blockY() + dy, block.blockZ() + dz)
            val blockAt = instance.getBlock(blockPos)
            if (!blockAt.nbtOrEmpty().getBoolean("shieldGenerator")) continue

            self.opposite.poi.shieldGenerator.damage(8 * explosionScore)

            return 4 * explosionScore
        }

        return 0
    }
}

