package org.everbuild.jam25.world.shield.generator

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.platform.minestom.api.utils.asVec
import org.everbuild.celestia.orion.platform.minestom.api.utils.pling
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.Jam
import org.everbuild.jam25.block.api.PlacementActor
import org.everbuild.jam25.block.api.ShieldGeneratorRefillComponent
import org.everbuild.jam25.block.impl.shieldgenerator.ShieldGeneratorBlock
import org.everbuild.jam25.item.api.get
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.world.Resource
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer

data class ShieldGenerator(
    val position: BlockVec,
    val direction: Direction
) : AdvanceableWorldElement {
    private val POWERLOSS_PER_MINUTE = 50.0
    private val POWERLOSS_PER_TICK = POWERLOSS_PER_MINUTE / (20 * 60)
    private val POWER_LEVEL_NOTIFICATIONS = listOf(10, 25)

    private val REFILL_POWERGAIN_PER_SECOND = 5.0
    private val REFILL_POWERGAIN_PER_TICK = REFILL_POWERGAIN_PER_SECOND / 20
    val refillBioScrapsPowerGain = 1.0
    val refillOilPowerGain = 1.0

    var team: GameTeam? = null

    var running: Boolean = ShieldGeneratorBlock.BlockState.DEFAULT.running
        set(value) {
            field = value
            ShieldGeneratorBlock.updateState(instance ?: return, position, value)

            team?.shield?.set(value)
            team?.homeBase?.enabled = !value
        }
    var power: Double = 100.0
        private set
    private var powerRenderer: ShieldGeneratorRefillRenderer? = null
    private var instance: Instance? = null
    private var group: DynamicGroup? = null
    private var pendingRefill = 0.0

    private val recheckCooldown = Cooldown(1.seconds)
    private val particleCooldown = Cooldown(250.milliseconds)

    fun setInstance(instance: Instance) {
        ShieldGeneratorBlock.placeBlock(instance, position, PlacementActor.ByTeam(team!!))
        this.instance = instance
        powerRenderer = ShieldGeneratorRefillRenderer(instance, position.asVec().add(0.5, 0.5, 0.5), direction)
    }

    fun setGroup(dynamicGroup: DynamicGroup) {
        group = dynamicGroup
    }

    fun registerRefillEvent(playerEvents: EventNode<PlayerEvent>) {
        playerEvents.listen<PlayerBlockInteractEvent, _> { event ->
            if (power + pendingRefill >= 99.0) return@listen
            val itemStack = event.player.getItemInHand(event.hand)
            if (itemStack.get<ShieldGeneratorRefillComponent>()?.canRefill != true) return@listen
            if (event.blockPosition !in ShieldGeneratorBlock.generatorPositions(position)) return@listen
            pendingRefill += refillBioScrapsPowerGain
            event.player.pling()

            if (event.player.gameMode != GameMode.CREATIVE) {
                val newItem = itemStack.withAmount(itemStack.amount() - 1)
                event.player.setItemInHand(event.hand, newItem)
            }
        }
    }

    fun inputDirection() = when(direction) {
        Direction.NORTH -> Direction.WEST
        Direction.EAST -> Direction.NORTH
        Direction.SOUTH -> Direction.EAST
        Direction.WEST -> Direction.SOUTH
        else -> Direction.NORTH
    }

    private fun recheckPipes() {
        val left = power + pendingRefill
        if (left >= 75.0) {
            return
        }

        val block = position.add(BlockVec(inputDirection().vec()))
        team!!.game.networkController.let {
            it.request(
                ItemConsumer.ItemOrOil.Oil(
                    20
                ),
                block,
                BlockFace.fromDirection(inputDirection())
            )

            it.request(
                ItemConsumer.ItemOrOil.Item(
                    Resource.BIO_SCRAPS.symbol.withAmount(
                        20
                    )
                ),
                block,
                BlockFace.fromDirection(inputDirection())

            )
        }
    }

    fun hasPower(): Boolean = power > 0

    fun refill(amount: Double) {
        if (amount + pendingRefill > 100.0) {
            pendingRefill = 100.0 - amount
        } else {
            pendingRefill += amount
        }
        if (pendingRefill > 100.0) pendingRefill = 100.0
        if (!running) running = true
        powerRenderer?.update(power, pendingRefill)
        team?.shield?.set(running)
    }

    override fun advance(instance: Instance) {
        if (recheckCooldown.get()) recheckPipes()
        if (pendingRefill > 0) {
            power = (power + REFILL_POWERGAIN_PER_TICK).coerceAtMost(100.0)
            pendingRefill = (pendingRefill - REFILL_POWERGAIN_PER_TICK).coerceAtLeast(0.0)
            if (power == 100.0) pendingRefill = 0.0
            if (!running) running = true
        } else {
            val powerBeforeLoss = power
            power = (power - POWERLOSS_PER_TICK).coerceAtLeast(0.0)
            for (notificationLevel in POWER_LEVEL_NOTIFICATIONS) {
                if (powerBeforeLoss > notificationLevel && power <= notificationLevel) {
                    group?.sendMiniMessage("${Jam.PREFIX} <red>Your shield generator is now at $notificationLevel% power level")
                }
            }
            if (!hasPower() && running) {
                running = false
                group?.sendMiniMessage("${Jam.PREFIX} <red>Your shield generator has run out of power")
            }
        }
        powerRenderer?.update(power, pendingRefill)

        if (running && particleCooldown.get()) {
            spawnParticles(instance)
        }
    }

    private fun spawnParticles(instance: Instance) {
        val from = position.add(0.5, 4.3, 0.5)
        team!!.poi.mainShield.toVertices().flatten().toSet().forEach {
            spawnTrail(100, from, Pos(it.x.toDouble(), it.y.toDouble(), it.z.toDouble()), instance)
        }

        instance.sendGroupedPacket(
            ParticlePacket(
                Particle.PORTAL,
                true, false,
                position.add(0.5, 3.5, 0.5),
                Pos(0.0, 0.5, 0.0),
                0.6f,
                50
            )
        )
    }

    private fun spawnTrail(steps: Int, from: Point, to: Point, instance: Instance) {
        val directionVec = to.sub(from).asVec()
        val perStep = directionVec.div(steps.toDouble())
        var currentPos = from
        (0..steps).forEach { step ->
            currentPos = currentPos.add(perStep)
            instance.sendGroupedPacket(
                ParticlePacket(
                    Particle.SCRAPE,
                    true, true,
                    currentPos,
                    Pos.ZERO,
                    0.3f,
                    1
                )
            )
        }
    }

    fun damage(i: Int) {
        val powerBeforeDamage = power
        power = (power - i).coerceAtLeast(0.0)

        if (powerBeforeDamage > 0 && !hasPower()) {
            running = false
        }
    }
    override fun getBlockPosition(): BlockVec = BlockVec(position)
}