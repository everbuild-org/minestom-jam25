package org.everbuild.jam25.world.shield.generator

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.platform.minestom.api.utils.pling
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.block.api.PlacementActor
import org.everbuild.jam25.block.api.ShieldGeneratorRefillComponent
import org.everbuild.jam25.block.impl.shieldgenerator.ShieldGeneratorBlock
import org.everbuild.jam25.item.api.get
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement

data class ShieldGenerator(
    val position: BlockVec
) : AdvanceableWorldElement {
    private val POWERLOSS_PER_MINUTE = 50.0
    private val POWERLOSS_PER_TICK = POWERLOSS_PER_MINUTE / (20 * 60)
    private val POWER_LEVEL_NOTIFICATIONS = listOf(10, 25)

    private val REFILL_POWERGAIN = 1.0
    private val REFILL_POWERGAIN_PER_SECOND = 5.0
    private val REFILL_POWERGAIN_PER_TICK = REFILL_POWERGAIN_PER_SECOND / 20

    var team: GameTeam? = null

    var running: Boolean = ShieldGeneratorBlock.BlockState.DEFAULT.running
        set(value) {
            field = value
            ShieldGeneratorBlock.updateState(instance ?: return, position, value)

            team?.shield?.set(value)
        }
    var power: Double = 100.0
        private set
    private var powerRenderer: ShieldGeneratorRefillRenderer? = null
    private var instance: Instance? = null
    private var group: DynamicGroup? = null
    private var pendingRefill = 0.0

    fun setInstance(instance: Instance) {
        ShieldGeneratorBlock.placeBlock(instance, position, PlacementActor.ByServer)
        this.instance = instance
        powerRenderer = ShieldGeneratorRefillRenderer(instance, position.asVec().add(0.5, 0.5, 0.5))
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
            pendingRefill += REFILL_POWERGAIN
            event.player.pling()

            if (event.player.gameMode != GameMode.CREATIVE) {
                val newItem = itemStack.withAmount(itemStack.amount() - 1)
                event.player.setItemInHand(event.hand, newItem)
            }
        }
    }

    fun hasPower(): Boolean = power > 0

    override fun advance(instance: Instance) {
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
                    group?.sendMiniMessage("<red>Your shield generator is now at $notificationLevel% power level")
                }
            }
            if (!hasPower() && running) {
                running = false
                group?.sendMiniMessage("<red>Your shield generator has run out of power")
            }
        }
        powerRenderer?.update(power, pendingRefill)
    }
}