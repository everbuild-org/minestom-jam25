package org.everbuild.jam25.world.vacuum

import kotlin.time.Duration.Companion.seconds
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.item.ItemEntityMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.platform.minestom.api.utils.dropItem
import org.everbuild.jam25.world.Resource
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer
import org.everbuild.jam25.world.placeable.ItemHolder

class Vacuum(val position: BlockVec) : AdvanceableWorldElement, ItemHolder {
    val fluidCooldown = Cooldown(1.seconds)
    val items = HashMap<Resource, Int>()
    val hologramLines = HashMap<Resource, ResourceHologramLine>()

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
        pullItems(instance)
        pickUpNear(instance)
        if (fluidCooldown.get()) pickUpFluid(instance)
        updateHologram(instance)
    }

    override fun getBlockPosition(): BlockVec = position

    private fun updateHologram(instance: Instance) {
        if (hologramLines.keys == items.keys) {
            hologramLines.forEach { (resource, hologram) -> hologram.amount = items[resource] ?: 0 }
            return
        }
        hologramLines.forEach { (_, hologram) -> hologram.remove() }
        hologramLines.clear()
        items.onEachIndexed { index, (resource, amount) ->
            hologramLines[resource] = ResourceHologramLine(position, index + 4, resource).also { it.amount = amount }
        }
        hologramLines.forEach { (_, hologram) -> hologram.setInstance(instance) }
    }

    private fun pullItems(instance: Instance) {
        instance.getNearbyEntities(position, 5.0)
            .mapPickupableItem()
            .forEach { (entity, _) ->
                val direction = entity.position.sub(position).asVec().normalize().mul(0.5)
                entity.velocity = direction.neg().withY(entity.velocity.y())
            }
    }

    private fun pickUpNear(instance: Instance) {
        instance.getNearbyEntities(position, 2.0)
            .mapPickupableItem()
            .forEach { (entity, meta) ->
                val resource = Resource.entries.find { meta.item.isSimilar(it.symbol) } ?: return@forEach
                entity.remove()
                items[resource] = (items[resource] ?: 0) + meta.item.amount()
            }
    }

    private fun Collection<Entity>.mapPickupableItem(): List<Pair<Entity, ItemEntityMeta>> {
        return filter { it.entityType == EntityType.ITEM }
            .mapNotNull {
                val meta = it.entityMeta as? ItemEntityMeta ?: return@mapNotNull null
                it to meta
            }
            .filter { (_, meta) -> Resource.entries.any { meta.item.isSimilar(it.symbol) } }
    }

    private fun pickUpFluid(instance: Instance) {
        for (x in -4..4) {
            for (y in -4..4) {
                for (z in -4..4) {
                    val block = instance.getBlock(position.add(x, y, z))
                    if (block.compare(Block.WATER)) {
                        items[Resource.OIL] = (items[Resource.OIL] ?: 0) + 1
                        return
                    }
                }
            }
        }
    }

    override fun hasItem(item: ItemConsumer.ItemOrOil): Boolean {
        when (item) {
            is ItemConsumer.ItemOrOil.Oil -> {
                return (items[Resource.OIL] ?: 0) > 0
            }

            is ItemConsumer.ItemOrOil.Item -> {
                val resource = Resource.entries.find { item.itemStack.isSimilar(it.symbol) } ?: return false
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
                val resource = Resource.entries.find { item.itemStack.isSimilar(it.symbol) } ?: return null
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
}