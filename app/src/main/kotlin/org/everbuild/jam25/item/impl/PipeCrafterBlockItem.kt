package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.attachCustomBlock
import org.everbuild.jam25.block.impl.crafting.PipeCrafterBlock
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.lore
import org.everbuild.jam25.item.api.name

object PipeCrafterBlockItem : AbstractItem(
    key = "pipe_crafter",
    item = itemStackOf(JamItems.pipeCrafterItem)
        .name("<gold>Pipe Crafter")
        .attachCustomBlock(PipeCrafterBlock)
        .lore("<gray>Pipe crafters are used to craft pipes, using <white>Metal Scraps<gray>.")
        .lore("<yellow>Connect pipes supplying <white>Metal Scraps<yellow> to start crafting.")
        .withMaxStackSize(16)
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}