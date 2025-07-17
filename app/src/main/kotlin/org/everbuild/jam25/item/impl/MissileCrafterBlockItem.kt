package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.attachCustomBlock
import org.everbuild.jam25.block.impl.crafting.MissileCrafterBlock
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.lore
import org.everbuild.jam25.item.api.name

object MissileCrafterBlockItem : AbstractItem(
    key = "missile_crafter",
    item = itemStackOf(JamItems.missileCrafterItem)
        .name("<gold>Missile Crafter")
        .attachCustomBlock(MissileCrafterBlock)
        .lore("<gray>Missile Crafters are used to craft missiles, using <white>Digital Components<gray> and <gold>Cable Components<gray>.")
        .lore("<yellow>Connect pipes supplying these items to start crafting.")
        .withMaxStackSize(16)
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}