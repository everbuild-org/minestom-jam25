package org.everbuild.jam25.item.api

import net.kyori.adventure.key.Key
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.CreativeInventoryActionEvent
import net.minestom.server.event.inventory.InventoryItemChangeEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.item.*
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.inventory.PlayerInventory
import org.everbuild.celestia.orion.platform.minestom.util.listen

fun withCustomItemListeners() {

    listen<ItemDropEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onDrop(event)
        }
    }

    listen<PickupItemEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onPickup(event)
        }
    }

    listen<InventoryPreClickEvent> { event ->
        event.clickedItem.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onClick(event)
        }
    }

    listen<PlayerBeginItemUseEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onBeginUse(event)
        }
    }

    listen<PlayerCancelItemUseEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onCancelUse(event)
        }
    }

    listen<PlayerFinishItemUseEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onFinishUse(event)
        }
    }

    listen<EntityEquipEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onEquip(event)
        }
    }

    listen<PlayerUseItemEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onUse(event)
        }
    }

    listen<PlayerUseItemOnBlockEvent> { event ->
        event.itemStack.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onUseOnBlock(event)
        }
    }

    listen<PlayerBlockBreakEvent> { event ->
        event.player.itemInMainHand.getTag(ItemLoader.typeTag)?.let {
            ItemLoader.byKey(Key.key(it))?.onHit(event.player, event, event)
        }
    }

    listen<CreativeInventoryActionEvent> {event ->
        if (ItemLoader.isCustomItem(event.clickedItem)) {
            return@listen
        }
        val customItem = ItemLoader.getOverriddenItem(event.clickedItem.material()) ?: return@listen
        event.clickedItem = customItem.createNewStack(event.clickedItem.amount())
    }

    listen<PickupItemEvent> { event ->
        if (event.entity !is Player) return@listen
        val player = event.entity as Player
        if (ItemLoader.isCustomItem(event.itemStack)) {
            return@listen
        }
        val customItem = ItemLoader.getOverriddenItem(event.itemStack.material()) ?: return@listen

        if (player.inventory.itemStacks.size == player.inventory.size
            && player.inventory.itemStacks.none { it.isSimilar(customItem.createNewStack(1))}) return@listen

        if (player.inventory.itemStacks
            .filter { it.isSimilar(customItem.createNewStack(1)) }
            .sumOf { it.maxStackSize() - it.amount() } > event.itemStack.amount()) return@listen

        event.isCancelled = true
        event.itemEntity.remove()
        player.inventory.addItemStack(customItem.createNewStack(event.itemStack.amount()))
    }

    listen<InventoryItemChangeEvent> {event ->
        if (ItemLoader.isCustomItem(event.newItem)) {
            return@listen
        }
        if (event.inventory !is PlayerInventory) return@listen

        val customItem = ItemLoader.getOverriddenItem(event.newItem.material()) ?: return@listen
        event.inventory.setItemStack(event.slot, customItem.createNewStack(event.newItem.amount()))
    }
}