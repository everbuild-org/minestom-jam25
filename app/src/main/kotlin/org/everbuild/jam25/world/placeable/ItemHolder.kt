package org.everbuild.jam25.world.placeable

import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.Jam
import org.everbuild.jam25.listener.dropItemOnFloor

interface ItemHolder {
    fun hasItem(item: ItemConsumer.ItemOrOil): Boolean
    fun removeItem(item: ItemConsumer.ItemOrOil): ItemConsumer.ItemOrOil?
    fun getItems(): List<ItemConsumer.ItemOrOil>
    fun clearItems()

    companion object {
        fun interactEventNode() = EventNode.all("item-holder-interactions")
            .listen<PlayerBlockInteractEvent, _> { event ->
                val game = Jam.gameStates.getInGamePhase(event.player) ?: return@listen
                val thing = game.getAdvanceable<ItemHolder>(event.blockPosition) ?: return@listen
                val items = thing.getItems().filterIsInstance<ItemConsumer.ItemOrOil.Item>()
                thing.clearItems()
                items.forEach { item ->
                    dropItemOnFloor(event.player.position, item.itemStack, event.player.instance)
                }
            }
    }
}