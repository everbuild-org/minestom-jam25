package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.attachCustomBlock
import org.everbuild.jam25.block.impl.vacuum.VacuumBlock
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.lore
import org.everbuild.jam25.item.api.name

object VacuumBlockItem : AbstractItem(
    key = "vacuum",
    item = itemStackOf(JamItems.vacuumItem)
        .name("<gold>Vacuum")
        .attachCustomBlock(VacuumBlock)
        .lore("<gray>Vacuums are used to collect items and fluids in a area around them.")
        .lore("<yellow>Range: 4 Blocks in each direction")
        .withMaxStackSize(16)
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}