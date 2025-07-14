package org.everbuild.jam25.item.api

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.item.*
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.event.trait.BlockEvent
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.jam25.util.toJamKey

abstract class AbstractItem(
    val id: Key,
    protected val item: ItemStack
) : IntoItem {
    open val overriddenMaterial: Material? = null
    open val maxDamage: Int? = null

    constructor(key: String, item: ItemStack) : this(key.toJamKey(), item)

    open fun createNewStack(amount: Int): ItemStack {
        return item
            .withAmount(amount)
            .withTag(ItemLoader.typeTag, id.asString())
            .builder().let {
                if (maxDamage != null && maxDamage!! >= 0) {
                    it.set(DataComponents.MAX_DAMAGE, maxDamage ?: -1)
                } else it
            }
            .build()
    }

    override fun createItem(): ItemStack = createNewStack(1)

    open fun onDrop(event: ItemDropEvent) {}
    open fun onPickup(event: PickupItemEvent) {}
    open fun onClick(event: InventoryPreClickEvent) {}
    open fun onBeginUse(event: PlayerBeginItemUseEvent) {}
    open fun onCancelUse(event: PlayerCancelItemUseEvent) {}
    open fun onFinishUse(event: PlayerFinishItemUseEvent) {}
    open fun onEquip(event: EntityEquipEvent) {}
    open fun onUse(event: PlayerUseItemEvent) {}
    open fun onUseOnBlock(event: PlayerUseItemOnBlockEvent) {}
    open fun onHit(player: Player, event: BlockEvent, cancel: CancellableEvent) {}
    open fun getTags(): List<String> = emptyList()
    open fun getPlacementHint(lookingAt: Block?): Component = Component.empty()
}