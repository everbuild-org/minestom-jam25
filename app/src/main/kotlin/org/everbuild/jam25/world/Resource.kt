package org.everbuild.jam25.world

import net.minestom.server.item.ItemStack
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.impl.BioScrapsItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.SiliconItem

enum class Resource(val symbol: ItemStack, val doDrop: Boolean) {
    BIO_SCRAPS(BioScrapsItem.createItem(), true),
    SILICON_DUST(SiliconItem.createItem(), true),
    METAL_SCRAPS(MetalScrapsItem.createItem(), true),
    OIL(itemStackOf(JamItems.oil), false)
}