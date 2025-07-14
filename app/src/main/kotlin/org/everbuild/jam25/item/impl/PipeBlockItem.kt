package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.attachCustomBlock
import org.everbuild.jam25.block.impl.pipe.PipeBlock
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object PipeBlockItem : AbstractItem(
    key = "pipe_block",
    item = itemStackOf(JamItems.pipeItem)
        .name("<color:#ff983d>Item & Fluid Pipe")
        .attachCustomBlock(PipeBlock)
        .withMaxStackSize(64)
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}