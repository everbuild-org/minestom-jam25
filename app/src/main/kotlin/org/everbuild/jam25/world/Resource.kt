package org.everbuild.jam25.world

import net.minestom.server.item.ItemStack
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.impl.BioScrapsItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.PipeBlockItem
import org.everbuild.jam25.item.impl.SiliconItem
import org.everbuild.jam25.world.placeable.ItemConsumer

enum class Resource(val symbol: ItemStack, val doDrop: Boolean) {
    BIO_SCRAPS(BioScrapsItem.createItem(), true),
    SILICON_DUST(SiliconItem.createItem(), true),
    METAL_SCRAPS(MetalScrapsItem.createItem(), true),
    PIPE(PipeBlockItem.createItem(), true),
    OIL(itemStackOf(JamItems.oil), false);

    fun toItemOrOil(amount: Int = 1): ItemConsumer.ItemOrOil {
        return if (doDrop) {
            ItemConsumer.ItemOrOil.Item(symbol.withAmount(amount))
        } else {
            ItemConsumer.ItemOrOil.Oil(amount)
        }
    }

    companion object {
        fun fromItem(itemStack: ItemStack): Resource? = Resource.entries.find { it.symbol.isSimilar(itemStack) }
        fun fromItemOrOil(itemOrOil: ItemConsumer.ItemOrOil): Resource? = when (itemOrOil) {
            is ItemConsumer.ItemOrOil.Oil -> OIL
            is ItemConsumer.ItemOrOil.Item -> fromItem(itemOrOil.itemStack)
        }
    }
}