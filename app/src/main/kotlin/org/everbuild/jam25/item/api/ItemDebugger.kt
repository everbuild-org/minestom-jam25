package org.everbuild.jam25.item.api

import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.items.SystemIcons
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger

object ItemDebugger : Debugger {
    override val identifier: String = "common/item"

    @Debuggable
    fun getItemWithModel(player: Player, model: String) {
        player.inventory.addItemStack(
            ItemStack.of(Material.IRON_HORSE_ARMOR, 1)
                .with(DataComponents.ITEM_MODEL, model)
                .withLore("Model: $model".component())
        )
    }

    @Debuggable
    fun getHourglassItem(player: Player) {
        player.inventory.addItemStack(
            ItemStack.of(Material.IRON_HORSE_ARMOR, 1)
                .with(DataComponents.ITEM_MODEL, SystemIcons.hourglass.model.asString())
        )
    }
}