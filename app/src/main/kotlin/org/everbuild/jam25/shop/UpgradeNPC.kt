package org.everbuild.jam25.shop

import java.util.concurrent.CompletableFuture
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.jam25.item.impl.DigitalComponentItem
import org.everbuild.jam25.item.impl.DigitalCrafterBlockItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.SiliconItem

class UpgradeNPC(val pos: Pos) : ShopNPC("Upgrades", SKIN_ENGINEER) {
    override fun setInstance(instance: Instance): CompletableFuture<Void?>? {
        return super.setInstance(instance, pos)
    }

    override fun openMenu(player: Player) {
        player.openInventory(ShopGUI("Upgrades", listOf(
            ShopGUI.ShopEntry.Item(MetalScrapsItem.createNewStack(2),null, ItemStack.of(Material.SPYGLASS)),
        )))
    }
}