package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.block.api.ShieldGeneratorRefillComponent
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name
import org.everbuild.jam25.item.api.with

object BioScrapsItem : AbstractItem(
    key = "bio_scraps",
    item = itemStackOf(JamItems.bioScraps)
        .name("<green>Bio Scraps")
        .withMaxStackSize(64)
        .with(ShieldGeneratorRefillComponent())
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null || !BlockController.canRefill(lookingAt, id.asString())) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}