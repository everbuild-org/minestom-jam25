package org.everbuild.jam25.item.impl

import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.jam25.block.api.attachCustomBlock
import org.everbuild.jam25.block.impl.launcher.MissileLauncherBlock
import org.everbuild.jam25.block.impl.missile1.Missile1Block
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.lore
import org.everbuild.jam25.item.api.name

object MissileLauncherItem : AbstractItem(
    key = "missile_launcher",
    item = itemStackOf(Material.PISTON)
        .name("<gold>Missile Launcher")
        .withMaxStackSize(64)
        .lore("<gray>Auto-explode your enemies!")
        .attachCustomBlock(MissileLauncherBlock)
) {
    override fun getPlacementHint(lookingAt: Block?): Component {
        if (lookingAt == null) return Component.empty()
        if (!lookingAt.compare(Block.MYCELIUM)) return Component.empty()
        return OrionPacks.getCharacterCodepoint(InteractionMenu.rightClick).component()
    }
}