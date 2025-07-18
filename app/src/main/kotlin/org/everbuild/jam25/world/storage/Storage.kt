package org.everbuild.jam25.world.storage

import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.platform.minestom.api.utils.dropItem
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.world.Resource
import org.everbuild.jam25.world.ResourceHologramLine
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer
import org.everbuild.jam25.world.placeable.ItemHolder

class Storage(val position: BlockVec, val team: GameTeam) : AdvanceableWorldElement, ItemHolder, ItemConsumer {
    val items = HashMap<Resource, Int>()
    val hologramLines = HashMap<Resource, ResourceHologramLine>()
    val cooldown = Cooldown(1.seconds)

    fun drop(instance: Instance) {
        hologramLines
            .forEach { (_, hologram) -> hologram.remove() }

        items
            .filter { (_, amount) -> amount > 0 }
            .filter { (res, _) -> res.doDrop }
            .forEach { (res, amount) ->
                var remaining = amount
                while (remaining > 0) {
                    val pickUp = res.symbol.maxStackSize().coerceAtMost(remaining)
                    instance.dropItem(res.symbol.withAmount(pickUp), position.add(0.5, 0.5, 0.5))
                    remaining -= pickUp
                }
            }
    }

    override fun advance(instance: Instance) {
        if (cooldown.get()) {
            for (vec in team.game.networkController.neighbouringPipes(position, instance)) {
                val dirVec = position.sub(vec).asVec().normalize()
                val dirs = Direction.entries.toTypedArray()
                var minDir = dirs[0]
                var minDot = 10000
                for (dir in dirs) {
                    val dot = dir.vec().dot(dirVec)
                    if (minDot > dot) {
                        minDir = dir
                        minDot = dot.roundToInt()
                    }
                }
                val dir = BlockFace.fromDirection(minDir)
                for (resource in Resource.entries) {
                    if (resource == Resource.OIL) continue
                    team.game.networkController.request(resource.toItemOrOil(5), position, dir)
                }
            }
        }
        updateHologram(instance)
    }

    override fun getBlockPosition(): BlockVec = position

    private fun updateHologram(instance: Instance) {
        if (hologramLines.keys == items.keys && !items.any { (_, amount) -> amount == 0 }) {
            hologramLines.forEach { (resource, hologram) -> hologram.amount = items[resource] ?: 0 }
            return
        }
        items.forEach { (res, amount) -> if (amount == 0) items.remove(res) }
        hologramLines.forEach { (_, hologram) -> hologram.remove() }
        hologramLines.clear()
        items.onEachIndexed { index, (resource, amount) ->
            hologramLines[resource] = ResourceHologramLine(position, index + 4, resource).also { it.amount = amount }
        }
        hologramLines.forEach { (_, hologram) -> hologram.setInstance(instance) }
    }

    override fun hasItem(item: ItemConsumer.ItemOrOil): Boolean {
        when (item) {
            is ItemConsumer.ItemOrOil.Oil -> {
                return (items[Resource.OIL] ?: 0) > 0
            }

            is ItemConsumer.ItemOrOil.Item -> {
                val resource = Resource.fromItem(item.itemStack) ?: return false
                return (items[resource] ?: 0) > 0
            }
        }
    }

    override fun removeItem(item: ItemConsumer.ItemOrOil): ItemConsumer.ItemOrOil? {
        when (item) {
            is ItemConsumer.ItemOrOil.Oil -> {
                val stackSize = item.amount
                val currentAmount = items[Resource.OIL] ?: 0
                if (currentAmount < stackSize) {
                    items.remove(Resource.OIL)
                    return ItemConsumer.ItemOrOil.Oil(stackSize)
                }
                items[Resource.OIL] = currentAmount - stackSize
                return ItemConsumer.ItemOrOil.Oil(stackSize)
            }

            is ItemConsumer.ItemOrOil.Item -> {
                val stackSize = item.itemStack.amount()
                val resource = Resource.fromItem(item.itemStack) ?: return null
                val currentAmount = items[resource] ?: 0
                if (currentAmount < stackSize) {
                    items.remove(resource)
                    return ItemConsumer.ItemOrOil.Item(item.itemStack.withAmount(currentAmount))
                }
                items[resource] = currentAmount - stackSize
                return ItemConsumer.ItemOrOil.Item(item.itemStack.withAmount(stackSize))
            }
        }
    }

    override fun getItems(): List<ItemConsumer.ItemOrOil> {
        return items.mapNotNull { (resource, amount) ->
            when (resource) {
                Resource.OIL -> ItemConsumer.ItemOrOil.Oil(amount)
                else -> ItemConsumer.ItemOrOil.Item(resource.symbol.withAmount(amount))
            }
        }
            .sortedBy { it.amount() }
            .toList()
    }

    override fun clearItems() {
        items.clear()
    }

    override fun consumeItem(item: ItemConsumer.ItemOrOil) {
        val resource = Resource.fromItemOrOil(item) ?: return
        items[resource] = (items[resource] ?: 0) + item.amount()
    }
}