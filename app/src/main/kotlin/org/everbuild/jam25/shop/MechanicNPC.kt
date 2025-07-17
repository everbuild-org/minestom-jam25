package org.everbuild.jam25.shop

import java.util.concurrent.CompletableFuture
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import org.everbuild.jam25.block.impl.crafting.CableComponentCrafterBlock
import org.everbuild.jam25.item.impl.CableComponentItem
import org.everbuild.jam25.item.impl.DigitalComponentItem
import org.everbuild.jam25.item.impl.DigitalCrafterBlockItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.Missile1Item
import org.everbuild.jam25.item.impl.MissileCrafterBlockItem
import org.everbuild.jam25.item.impl.MissileLauncherItem
import org.everbuild.jam25.item.impl.PipeBlockItem
import org.everbuild.jam25.item.impl.PipeCrafterBlockItem
import org.everbuild.jam25.item.impl.SiliconItem
import org.everbuild.jam25.item.impl.VacuumBlockItem

class MechanicNPC(val pos: Pos) : ShopNPC("Mechanic", SKIN_MECHANIC) {
    override fun setInstance(instance: Instance): CompletableFuture<Void?>? {
        return super.setInstance(instance, pos)
    }

    override fun openMenu(player: Player) {
        player.openInventory(ShopGUI("Mechanic", listOf(
            ShopGUI.ShopEntry.Item(SiliconItem.createNewStack(3), MetalScrapsItem.createItem(), DigitalComponentItem.createItem()),
            ShopGUI.ShopEntry.Item(MetalScrapsItem.createNewStack(2), null, CableComponentItem.createItem()),
            ShopGUI.ShopEntry.Item(DigitalComponentItem.createItem(), CableComponentItem.createItem(), Missile1Item.createItem()),
            ShopGUI.ShopEntry.Item(MetalScrapsItem.createNewStack(2), null, PipeBlockItem.createNewStack(2)),
            ShopGUI.ShopEntry.Item(MetalScrapsItem.createNewStack(3), DigitalComponentItem.createItem(), VacuumBlockItem.createItem()),
            ShopGUI.ShopEntry.Item(PipeBlockItem.createNewStack(2), DigitalComponentItem.createItem(), MissileLauncherItem.createItem()),
            ShopGUI.ShopEntry.Item(SiliconItem.createNewStack(3), DigitalComponentItem.createItem(), MissileCrafterBlockItem.createItem()),
            ShopGUI.ShopEntry.Item(MetalScrapsItem.createNewStack(5), DigitalComponentItem.createItem(), PipeCrafterBlockItem.createItem()),
            ShopGUI.ShopEntry.Item(SiliconItem.createNewStack(2), DigitalComponentItem.createItem(), DigitalCrafterBlockItem.createItem()),
            ShopGUI.ShopEntry.Item(PipeBlockItem.createNewStack(3), DigitalComponentItem.createItem(), CableComponentCrafterBlock.createItem()),
        )))
    }
}