package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.block.api.WrenchComponent
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name
import org.everbuild.jam25.item.api.with

object HammerItem : AbstractItem(
    key = "hammer",
    item = itemStackOf(JamItems.hammer)
        .name("<yellow>Construction Tool")
        .with(WrenchComponent())
) {
    override fun getPlacementHint(lookingAt: Block?): TextComponent {
        if (lookingAt == null) return Component.empty()
        if (!BlockController.canBreak(lookingAt)) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}