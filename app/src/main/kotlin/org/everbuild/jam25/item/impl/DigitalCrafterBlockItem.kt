package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.attachCustomBlock
import org.everbuild.jam25.block.impl.crafting.CableComponentCrafterBlock
import org.everbuild.jam25.block.impl.crafting.DigitalComponentCrafterBlock
import org.everbuild.jam25.block.impl.crafting.MissileCrafterBlock
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.lore
import org.everbuild.jam25.item.api.name

object DigitalCrafterBlockItem : AbstractItem(
    key = "digital_crafter",
    item = itemStackOf(JamItems.digitalCrafterItem)
        .name("<gold>Digital Component Crafter")
        .attachCustomBlock(DigitalComponentCrafterBlock)
        .lore("<gray>Digital Component crafters are used to craft pipes, using <white>Metal Scraps<gray> and <white>Silicon Dust<gray>.")
        .lore("<yellow>Connect pipes supplying these items to start crafting.")
        .withMaxStackSize(16)
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}